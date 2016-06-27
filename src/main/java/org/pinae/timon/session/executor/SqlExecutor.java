package org.pinae.timon.session.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.pinae.timon.sql.Sql;


/**
 * 数据库访问执行器
 * 
 * @author Huiyugeng
 *
 */
public class SqlExecutor extends SqlStatement {
	
	private static Logger logger = Logger.getLogger(SqlExecutor.class);
	
	private Connection conn = null;
	
	public SqlExecutor(Connection conn) throws NullPointerException {
		if (conn != null) {
			this.conn = conn;
		} else {
			throw new NullPointerException("Connection is NULL");
		}
	}

	
	/**
	 * 执行SQL查询
	 * 
	 * @param sql 需要执行的查询SQL语句对象
	 * 
	 * @return 查询结果列表
	 */
	public List<Object[]> select(Sql sql) {
		
		long start =  System.currentTimeMillis();
		
		if (sql.validate() == false) {
			return null;
		}
		
		List<Object[]> dataList = null;
		
		if (sql.isSelect()) {

			dataList = new ArrayList<Object[]>();
			
			String query = sql.getSql();
			
			ResultSet rs = null;
			PreparedStatement stmt = null;

			try {
				stmt = this.createStatment(conn, sql);
				rs = stmt.executeQuery();
				
				ResultSetMetaData rsmd = rs.getMetaData();

				int columnCount = rsmd.getColumnCount();
				while (rs.next()) {
					Object[] row = new Object[columnCount];
					for (int i = 0; i < columnCount; i++) {
						row[i] = rs.getObject(i + 1);
					}
					dataList.add(row);
				}

			} catch (SQLException e) {
				logger.error(String.format("select Exception: exception=%s; sql=%s", e.getMessage(), query));
			} finally {
				try {
					if (rs != null && rs.isClosed() == false) {
						rs.close();
					}
					if (stmt != null && stmt.isClosed() == false) {
						stmt.close();
					}
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		
		logger.debug(String.format("Execute %s used %d ms", sql.getSql(), System.currentTimeMillis() - start));
		
		return dataList;
	}
	
	/**
	 * 执行非查询 SQL语句
	 * 
	 * @param sql 需要执行的非查询SQL语句对象(insert/update/delete)
	 * 
	 * @return 是否执行成功
	 */
	public boolean execute(Sql sql) {
		
		long start =  System.currentTimeMillis();
		
		if (sql.validate() == false) {
			return false;
		}
		
		boolean result = false;
		
		Statement stmt = null;
		try {
			stmt = this.createStatment(conn, sql);
			stmt.execute(sql.getSql());
			
			result = true;
		} catch (SQLException e) {
			logger.error(String.format("execute Exception: exception=%s; sql=%s", e.getMessage(), sql));
		} finally {
			try {
				if (stmt != null && stmt.isClosed() == false) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		logger.debug(String.format("Execute %s used %d ms", sql.getSql(), System.currentTimeMillis() - start));

		return result;
	}
	
	/**
	 * 批量执行SQL语句 
	 * 
	 * @param sqls SQL语句集合
	 * @param batchSize 每批次执行数量
	 * 
	 * @return 批量执行结果
	 */
	public int[] execute(Iterable<String> sqls, int batchSize) {
		if (sqls == null) {
			return null;
		}
		
		List<int[]> tmpResultList = new ArrayList<int[]>();
		
		Statement stmt = null;
		try {
			int counter = 0;
			stmt = conn.createStatement();
			for (String sql : sqls) {
				stmt.addBatch(sql);
				counter ++;
				if (batchSize > 0 && counter % batchSize == 0) {
					tmpResultList.add(stmt.executeBatch());
				}
			}
			tmpResultList.add(stmt.executeBatch());

		} catch (SQLException e) {
			logger.error(String.format("execute Exception: exception=%s", e.getMessage()));
		} finally {
			try {
				if (stmt != null && stmt.isClosed() == false) {
					stmt.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		
		int[] result = new int[0];
		for (int[] tmpResult : tmpResultList) {
			int[] tmpArray = new int[result.length + tmpResult.length];
			
			System.arraycopy(result, 0, tmpArray, 0, result.length);
			System.arraycopy(tmpResult, 0, tmpArray, result.length, tmpResult.length);
			
			result = tmpArray;
		}

		return result;
	}
	
	/**
	 * SQL事务: 提交
	 */
	public void commit() {
		try {
			conn.commit();
		} catch (SQLException e) {
			logger.error(String.format("commit Exception: exception=%s", e.getMessage()));
		}
	}

	/**
	 * SQL事务: 回滚
	 */
	public void rollback() {
		try {
			conn.rollback();
		} catch (SQLException e) {
			logger.error(String.format("rollback Exception: exception=%s", e.getMessage()));
		}
	}

	/**
	 * 关闭数据库连接
	 */
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			logger.error(String.format("close Exception: exception=%s", e.getMessage()));
		}
	}
	
	/**
	 * 判断数据库连接是否关闭
	 * 
	 * @return 数据库是否关闭
	 */
	public boolean isClosed() {
		try {
			if (conn != null) {
				return conn.isClosed();
			}
		} catch (SQLException e) {
			logger.error(String.format("isClosed Exception: exception=%s", e.getMessage()));
		}
		return true;
	}

}
