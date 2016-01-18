package org.pinae.timon.session.defaults;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
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
import org.pinae.timon.sql.Sql;
import org.pinae.timon.sql.SqlBuilder;
import org.pinae.timon.util.ConfigMap;

/**
 * 默认数据库会话管理
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
	 * @param sessionConfig 数据库配置信息
	 * 
	 * @throws IOException 数据库IO异常
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

	public Object[] one(Sql sql, ResultHandler handler) {
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

	public Object[] one(Sql sql) {
		return one(sql, (ResultHandler) null);
	}

	@SuppressWarnings("unchecked")
	public <T> T one(Sql sql, Class<T> clazz, ResultHandler handler) {
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

	public <T> T one(Sql sql, Class<T> clazz) {
		return one(sql, clazz, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T one(Sql sql, String[] columns, Class<T> clazz, ResultHandler handler) {
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

	public <T> T one(Sql sql, String[] columns, Class<T> clazz) {
		return one(sql, columns, clazz);
	}

	public List<Object[]> select(Sql sql) {
		return select(sql, (ResultHandler) null);
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> select(Sql sql, ResultHandler handler) {

		if (StringUtils.isBlank(sql.getSql())) {
			throw new NullPointerException("SQL is NULL");
		}

		List<Object[]> queryResult = null;

		try {
			String sqlKey = sql.getDigest();

			boolean isCacheSql = this.isCacheSql;

			// 检查SQL语句中禁止缓存结果
			Map<String, String> comment = sql.getComment();
			if ("FALSE".equals(comment.get("CACHE"))) {
				isCacheSql = false;
			}

			if (this.cache != null && isCacheSql == true) {
				queryResult = (List<Object[]>) this.cache.get(sqlKey);
			}

			if (queryResult == null) {
				// 打印SQL语句
				printSql(sql);
				
				// 执行Select
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

	public List<?> select(Sql sql, String[] columns, Class<?> clazz, ResultHandler handler) {
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

	public List<?> select(Sql sql, String[] columns, Class<?> clazz) {
		return select(sql, columns, clazz, null);
	}

	public List<?> select(Sql sql, Class<?> clazz, ResultHandler handler) {
		String columns[] = getColumnsBySql(sql);
		return select(sql, columns, clazz, handler);
	}

	public List<?> select(Sql sql, Class<?> clazz) {
		return select(sql, clazz, null);
	}

	public long count(Sql sql) {
		String query = sql.getSql();
		if (StringUtils.isEmpty(query)) {
			return 0;
		} else {
			query = query.trim();
		}
		// 如果SQL语句中不包含count关键字，则构建一个计数SQL
		if (!StringUtils.containsIgnoreCase(query, "count")) {
			query = SqlBuilder.getCountSQL(query);
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

	public boolean execute(Sql sql) {
		printSql(sql);
		return this.executor.execute(sql);
	}

	public int[] execute(Iterable<String> sqls) {
		return this.execute(sqls, 0);
	}
	
	public int[] execute(Iterable<String> sqls, int batchSize) {
		if (sqls == null) {
			return null;
		}
		for (String sql : sqls) {
			printSql(new Sql(sql));
		}
		return this.executor.execute(sqls, batchSize);
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

	public String[] getColumnsBySql(Sql sql) {
		SqlMetadata metadata = getMetadata();
		if (metadata != null) {
			return metadata.getColumnsBySql(sql);
		}
		return null;
	}

	private void printSql(Sql sql) {
		
		String query = sql.getSql();
		Map<String, String> comment = sql.getComment();
		
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
			
			if (level.equals("DEBUG")) {
				logger.debug(query);
			} else if (level.equals("INFO")) {
				logger.info(query);
			} else if (level.equals("WARN")) {
				logger.warn(query);
			} else if (level.equals("ERROR")) {
				logger.error(query);
			}
		}
	}


}
