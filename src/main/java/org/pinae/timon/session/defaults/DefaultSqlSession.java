package org.pinae.timon.session.defaults;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.pinae.timon.cache.Cache;
import org.pinae.timon.cache.CacheException;
import org.pinae.timon.reflection.AnnotationReflector;
import org.pinae.timon.reflection.MapReflector;
import org.pinae.timon.reflection.ObjectReflector;
import org.pinae.timon.reflection.Reflector;
import org.pinae.timon.reflection.annotation.Entity;
import org.pinae.timon.session.SqlSession;
import org.pinae.timon.session.executor.SqlExecutor;
import org.pinae.timon.session.executor.SqlMetadata;
import org.pinae.timon.session.handle.ResultHandler;
import org.pinae.timon.sql.SqlBuilder;
import org.pinae.timon.util.ConfigMap;
import org.pinae.timon.util.MessageDigestUtils;

/**
 * 数据库会话管理
 * 
 * @author Huiyugeng
 *
 */
public class DefaultSqlSession implements SqlSession {

	private static Logger logger = Logger.getLogger(SqlSession.class);

	private Connection connection;

	private SqlExecutor executor;
	private SqlMetadata metadata;

	private Cache cache;

	private ConfigMap<String, String> sessionConfig;

	private boolean isCacheSql = true; // 全局SQL缓存开关, 默认开启缓存
	private boolean isShowSql = false; // SQL显示开关, 默认不显示SQL

	/**
	 * 构造函数
	 * 
	 * @param conn 数据库连接
	 * @param cache SQL缓存
	 * @param config 数据库配置信息
	 * 
	 * @throws IOException IO异常
	 */
	public DefaultSqlSession(Connection conn, Cache cache, ConfigMap<String, String> sessionConfig) throws IOException {

		this.connection = conn;
		this.cache = cache;
		this.sessionConfig = sessionConfig;

		if (conn != null) {
			this.executor = new SqlExecutor(conn);
			this.metadata = new SqlMetadata(conn);
		} else {
			throw new IOException("Connection is NULL");
		}

		// 检查Session配置文件中缓存禁止缓存结果
		if ("DISABLE".equals(this.sessionConfig.getString("sql.cache", "ENABLE").toUpperCase())) {
			isCacheSql = false;
		}

		// 检查Session配置文件中是否显示SQL
		if ("ENABLE".equals(this.sessionConfig.getString("sql.show", "DISABLE").toUpperCase())) {
			isShowSql = true;
		}
	}

	public Connection getConnection() {
		return this.connection;
	}

	public SqlMetadata getMetadata() {
		return this.metadata;
	}

	public SqlExecutor getExecutor() {
		return this.executor;
	}

	public Object[] one(String sql, ResultHandler handler) {
		Object[] result = null;
		List<Object[]> table = select(sql);
		if (table != null && table.size() > 0) {
			result = table.get(0);
			if (handler != null) {
				handler.handle(result);
			}
		}
		return result;
	}

	public Object[] one(String sql) {
		return one(sql, (ResultHandler) null);
	}

	@SuppressWarnings("unchecked")
	public <T> T one(String sql, Class<T> clazz, ResultHandler handler) {
		T result = null;
		List<?> table = select(sql, clazz);
		if (table != null && table.size() > 0) {
			result = (T) table.get(0);
			if (handler != null) {
				handler.handle(result);
			}
		}
		return result;
	}

	public <T> T one(String sql, Class<T> clazz) {
		return one(sql, clazz, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T one(String sql, String[] columns, Class<T> clazz, ResultHandler handler) {
		T result = null;
		List<?> table = select(sql, columns, clazz);
		if (table != null && table.size() > 0) {
			result = (T) table.get(0);
			if (handler != null) {
				handler.handle(result);
			}
		}
		return result;
	}

	public <T> T one(String sql, String[] columns, Class<T> clazz) {
		return one(sql, columns, clazz);
	}

	public List<Object[]> select(String sql) {
		return select(sql, (ResultHandler) null);
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> select(String sql, ResultHandler handler) {

		if (StringUtils.isBlank(sql)) {
			throw new NullPointerException("SQL is NULL");
		}

		List<Object[]> queryResult = null;

		try {
			String sqlKey = MessageDigestUtils.MD5(sql);

			boolean isCacheSql = this.isCacheSql;

			// 检查SQL语句中禁止缓存结果
			Map<String, String> comment = parseSqlComment(sql);
			if ("FALSE".equals(comment.get("CACHE"))) {
				isCacheSql = false;
			}

			if (this.cache != null && isCacheSql == true) {
				queryResult = (List<Object[]>) this.cache.get(sqlKey);
			}

			if (queryResult == null) {
				printSql(sql, comment);
				queryResult = this.executor.select(sql);

				if (handler != null) {
					handler.handle(queryResult);
				}

				if (this.cache != null && isCacheSql == true) {
					int expire = -1;
					if (StringUtils.isNumeric(comment.get("CACHE"))) {
						try {
							expire = Integer.parseInt(comment.get("CACHE"));
						} catch (NumberFormatException e) {
							expire = -1;
						}
					}
					if (expire > 0) {
						this.cache.put(sqlKey, queryResult, expire);
					} else {
						this.cache.put(sqlKey, queryResult);
					}
				}
			}
		} catch (CacheException e) {

		}
		return queryResult;
	}

	public List<?> select(String sql, String[] columns, Class<?> clazz, ResultHandler handler) {
		List<Object[]> dataList = select(sql, handler);

		if (clazz == null) {
			clazz = Map.class;
		}

		Reflector reflector = null;
		if (clazz.equals(Map.class)) {
			reflector = new MapReflector();
		} else {
			if (clazz.isAnnotationPresent(Entity.class)) {
				reflector = new AnnotationReflector(clazz);
			} else {
				reflector = new ObjectReflector(clazz);
			}
		}

		List<?> table = null;
		if (reflector != null) {
			table = reflector.toList(dataList, columns);
		}

		if (handler != null) {
			handler.handle(table);
		}

		return table;
	}

	public List<?> select(String sql, String[] columns, Class<?> clazz) {
		return select(sql, columns, clazz, null);
	}

	public List<?> select(String sql, Class<?> clazz, ResultHandler handler) {
		String columns[] = getColumnsBySql(sql);
		return select(sql, columns, clazz, handler);
	}

	public List<?> select(String sql, Class<?> clazz) {
		return select(sql, clazz, null);
	}

	public long count(String sql) {
		if (StringUtils.isEmpty(sql)) {
			return 0;
		} else {
			sql = sql.trim();
		}
		// 如果SQL语句中不包含count关键字，则构建一个计数SQL
		if (!StringUtils.containsIgnoreCase(sql, "count")) {
			sql = SqlBuilder.getCountSQL(sql);
		}

		long count = 0;
		List<Object[]> table = select(sql);
		if (table.size() == 1) {
			Object[] row = table.get(0);
			if (row != null && row.length == 1) {
				Object value = row[0];
				if (value != null) {
					if (value instanceof BigDecimal) {
						count = ((BigDecimal) value).longValue();
					} else if (value instanceof Long) {
						count = (Long) value;
					} else if (value instanceof Integer) {
						count = (Integer) value;
					}
				}
			}
		}

		return count;
	}

	public boolean execute(String sql) {
		printSql(sql);
		return this.executor.execute(sql);
	}

	public boolean execute(List<String> sqlList) {
		for (String sql : sqlList) {
			printSql(sql);
		}
		return this.executor.execute(sqlList);
	}

	public void commit() {
		this.executor.commit();
	}

	public void rollback() {
		this.executor.rollback();
	}

	public void close() {
		this.executor.close();
	}

	public boolean isClosed() {
		return this.executor.isClosed();
	}

	public String[] getColumnsBySql(String sql) {
		SqlMetadata metadata = getMetadata();
		if (metadata != null) {
			return metadata.getColumnsBySql(sql);
		}
		return null;
	}

	private Map<String, String> parseSqlComment(String sql) {
		Map<String, String> commentMap = new HashMap<String, String>();
		try {
		while (StringUtils.contains(sql, "/*") && StringUtils.contains(sql, "*/")) {
			String comment = StringUtils.substringBetween(sql, "/*", "*/");
			comment = comment.toUpperCase().trim();

			if (StringUtils.isNotEmpty(comment)) {
				if (comment.contains("NO_CACHE")) {
					commentMap.put("CACHE", "FALSE");
				}
				String commentItems[] = comment.split(",");
				for (String commentItem : commentItems) {
					if (commentItem != null && commentItem.contains("=")) {
						String value[] = commentItem.trim().split("=");
						if (value != null && value.length == 2) {
							commentMap.put(value[0].trim(), value[1].trim());
						}
					}
				}
			}

			sql = StringUtils.substringBefore(sql, "/*") + StringUtils.substringAfter(sql, "*/");
		}
		} catch (Exception e) {
			logger.error("Parse sql comment fail:" + sql);
		}
		return commentMap;
	}
	
	private void printSql(String sql) {
		 Map<String, String> comment = parseSqlComment(sql);
		 printSql(sql, comment);
	}

	private void printSql(String sql, Map<String, String> comment) {
		boolean isShowSql = this.isShowSql;

		if (comment.containsKey("SHOW") && !"FALSE".equals(comment.get("SHOW"))) {
			isShowSql = true;
		}
		if (isShowSql) {
			String level = comment.get("SHOW");
			if (StringUtils.isEmpty(level)) {
				level = "DEBUG";
			}
			level = level.toUpperCase();
			if (!StringUtils.containsAny(level, "DEBUG", "INFO", "WARN", "ERROR")) {
				level = "DEBUG";
			}
			switch (level) {
			case "DEBUG":
				logger.debug(sql);
				break;
			case "INFO":
				logger.info(sql);
				break;
			case "WARN":
				logger.warn(sql);
				break;
			case "ERROR":
				logger.error(sql);
				break;
			}
		}
	}

}
