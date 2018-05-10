package org.pinae.timon.session;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.pinae.timon.session.executor.SqlExecutor;
import org.pinae.timon.session.executor.SqlMetadata;
import org.pinae.timon.session.handle.ResultHandler;
import org.pinae.timon.sql.Sql;

/**
 * 数据库会话管理
 * 
 * @author Huiyugeng
 *
 */
public interface SqlSession {
	
	public Connection getConnection();
	
	public SqlMetadata getMetadata();
	
	public SqlExecutor getExecutor();
	
	public Object[] one(Sql sql) throws SQLException;
	
	public Object[] one(Sql sql, ResultHandler handler) throws SQLException;
	
	public <T> T one(Sql sql, Class<T> clazz) throws SQLException;
	
	public <T> T one(Sql sql, Class<T> clazz, ResultHandler handler) throws SQLException;
	
	public <T> T one(Sql sql, String[] columns, Class<T> clazz) throws SQLException;
	
	public <T> T one(Sql sql, String[] columns, Class<T> clazz, ResultHandler handler) throws SQLException; 

	public List<Object[]> select(Sql sql) throws SQLException;
	
	public List<Object[]> select(Sql sql, ResultHandler handler) throws SQLException;
	
	public List<?> select(Sql sql, String[] columns, Class<?> clazz) throws SQLException;
	
	public List<?> select(Sql sql, String[] columns, Class<?> clazz, ResultHandler handler) throws SQLException;
	
	public List<?> select(Sql sql, Class<?> clazz) throws SQLException;
	
	public List<?> select(Sql sql, Class<?> clazz, ResultHandler handler) throws SQLException;
	
	public long count(Sql sql) throws SQLException;

	public boolean execute(Sql sql) throws SQLException;

	public int[] execute(Iterable<String> sqls) throws SQLException;
	
	public int[] execute(Iterable<String> sqls, int batchSize) throws SQLException;
	
	public Object[] call(Sql sql) throws SQLException;
	
	public void commit() throws SQLException;

	public void rollback() throws SQLException;

	public void close() throws SQLException;
	
	public boolean isClosed() throws SQLException;

	public String[] getColumnsBySql(Sql sql) throws SQLException;
}
