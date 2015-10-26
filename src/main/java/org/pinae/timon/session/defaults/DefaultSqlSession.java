package org.pinae.timon.session.defaults;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import org.pinae.timon.util.MessageDigestUtils;

/**
 * 数据库会话管理
 * 
 * @author Huiyugeng
 *
 */
public class DefaultSqlSession implements SqlSession {

	private Connection connection;

	private SqlExecutor executor;
	private SqlMetadata metadata;

	private String dbType;

	private Cache cache;

	public DefaultSqlSession(String dbType, Connection conn, Cache cache) throws IOException {
		this.dbType = dbType;
		this.connection = conn;

		this.cache = cache;

		if (conn != null) {
			this.executor = new SqlExecutor(conn);
			this.metadata = new SqlMetadata(conn);
		} else {
			throw new IOException("Connection is NULL");
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
		return one(sql, new ResultHandler() {
			public <T> void handle(T t) {
			}
		});
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
		return select(sql, new ResultHandler() {
			public <T> void handle(T t) {
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> select(String sql, ResultHandler handler) {
		List<Object[]> result = null;
		
		try {
			String sqlKey = MessageDigestUtils.MD5(sql);
			
			boolean enableCache = true;
			Map<String, String> comment = parseSqlComment(sql);
			if ("FALSE".equals(comment.get("CACHE"))){
				enableCache = false;
			}
			if (this.cache != null && enableCache == true) {
				result = (List<Object[]>)this.cache.get(sqlKey);
			}
			
			if (result == null) {
				result = this.executor.select(sql);
				
				if (handler != null) {
					handler.handle(result);
				}
				
				if (this.cache != null && enableCache == true) {
					int expire = -1;
					if (StringUtils.isNumeric(comment.get("CACHE"))) {
						try {
							expire = Integer.parseInt(comment.get("CACHE"));
						} catch (NumberFormatException e) {
							expire = -1;
						}
					}
					this.cache.put(sqlKey, result, expire);
				}
			}
		} catch (CacheException e) {
			
		}
		return result;
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
		return this.executor.execute(sql);
	}

	public boolean execute(List<String> sqlList) {
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

	public String getDbType() {
		return dbType;
	}
	
	private Map<String, String> parseSqlComment(String sql) {
		Map<String, String> commentMap = new HashMap<String, String>();
		while (StringUtils.contains(sql, "/*") && StringUtils.contains(sql, "*/")) {
			String comment = StringUtils.substringBetween(sql, "/*", "*/");
			comment = comment.toUpperCase().trim();
			
			if (StringUtils.isNotEmpty(comment)) {
				if (comment.contains("NO_CACHE")) {
					commentMap.put("CACHE", "FALSE");
				}
				String commentItems[] = comment.split(",");
				for (String commentItem : commentItems) {
					if (commentItem.contains("=")) {
						String value[] = commentItem.split("=");
						if (value != null && value.length == 2) {
							commentMap.put(value[0], value[1]);
						}
					}
				}
			}
			
			sql = StringUtils.substringBefore(sql, "/*") + StringUtils.substringAfter(sql, "*/");
		}
		return commentMap;
	}

}
