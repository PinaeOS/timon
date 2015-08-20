package org.pinae.timon.session;

import java.sql.Connection;
import java.util.List;

import org.pinae.timon.session.executor.SqlExecutor;
import org.pinae.timon.session.executor.SqlMetadata;
import org.pinae.timon.session.handle.ResultHandler;

public interface SqlSession {
	
	public Connection getConnection();
	
	public SqlMetadata getMetadata();
	
	public SqlExecutor getExecutor();
	
	public Object[] one(String sql) ;
	
	public Object[] one(String sql, ResultHandler handler);
	
	public <T> T one(String sql, Class<T> clazz);
	
	public <T> T one(String sql, Class<T> clazz, ResultHandler handler);
	
	public <T> T one(String sql, String[] columns, Class<T> clazz);
	
	public <T> T one(String sql, String[] columns, Class<T> clazz, ResultHandler handler); 

	public List<Object[]> select(String sql);
	
	public List<Object[]> select(String sql, ResultHandler handler);
	
	public List<?> select(String sql, String[] columns, Class<?> clazz);
	
	public List<?> select(String sql, String[] columns, Class<?> clazz, ResultHandler handler);
	
	public List<?> select(String sql, Class<?> clazz);
	
	public List<?> select(String sql, Class<?> clazz, ResultHandler handler);
	
	public long count(String sql);

	public boolean execute(String sql);

	public boolean execute(List<String> sqlList);
	
	public void commit();

	public void rollback();

	public void close();
	
	public boolean isClosed();

	public String[] getColumnsBySql(String sql);

	public String getDbType();
}
