package org.pinae.timon.session;

import java.sql.Connection;
import java.util.List;

import org.pinae.timon.session.executor.SqlExecutor;
import org.pinae.timon.session.executor.SqlMetadata;
import org.pinae.timon.session.handle.ResultHandler;
import org.pinae.timon.sql.Sql;

public interface SqlSession {
	
	public Connection getConnection();
	
	public SqlMetadata getMetadata();
	
	public SqlExecutor getExecutor();
	
	public Object[] one(Sql sql) ;
	
	public Object[] one(Sql sql, ResultHandler handler);
	
	public <T> T one(Sql sql, Class<T> clazz);
	
	public <T> T one(Sql sql, Class<T> clazz, ResultHandler handler);
	
	public <T> T one(Sql sql, String[] columns, Class<T> clazz);
	
	public <T> T one(Sql sql, String[] columns, Class<T> clazz, ResultHandler handler); 

	public List<Object[]> select(Sql sql);
	
	public List<Object[]> select(Sql sql, ResultHandler handler);
	
	public List<?> select(Sql sql, String[] columns, Class<?> clazz);
	
	public List<?> select(Sql sql, String[] columns, Class<?> clazz, ResultHandler handler);
	
	public List<?> select(Sql sql, Class<?> clazz);
	
	public List<?> select(Sql sql, Class<?> clazz, ResultHandler handler);
	
	public long count(Sql sql);

	public boolean execute(Sql sql);

	public boolean execute(List<String> sqlList);
	
	public void commit();

	public void rollback();

	public void close();
	
	public boolean isClosed();

	public String[] getColumnsBySql(Sql sql);
}
