package org.pinae.timon.session.defaults;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
	
	
	public DefaultSqlSession(String dbType, Connection conn) throws IOException {
		this.dbType = dbType;
		this.connection = conn;
		
		if(conn != null) {
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
		return one(sql, new ResultHandler(){
			public <T> void handle(T t) { }
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
		return select(sql, new ResultHandler(){
			public <T> void handle(T t) { }
		});
	}
	
	public List<Object[]> select(String sql, ResultHandler handler) {
		List<Object[]> result = this.executor.select(sql);
		if (handler != null) {
			handler.handle(result);
		}
		return result;
	}
	
	public List<?> select(String sql, String[] columns, Class<?> clazz, ResultHandler handler) {
		List<Object[]> dataList = select(sql);
		
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
		List<?> table = null;
		
		String columns[] = getColumnsBySql(sql);
		if (columns != null) {
			table = select(sql, columns, clazz);
		}
		
		if (handler != null) {
			handler.handle(table);
		}
		
		return table;
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
		//如果SQL语句中不包含count关键字，则构建一个计数SQL
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
						count = ((BigDecimal)value).longValue();
					} else if (value instanceof Long) {
						count = (Long)value;
					} else if (value instanceof Integer) {
						count = (Integer)value;
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





}
