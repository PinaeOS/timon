package org.pinae.timon.session.executor;

import java.sql.CallableStatement;
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
	 * 
	 * @throws SQLException 
	 */
	public List<Object[]> select(Sql sql) throws SQLException {
		
		long start =  System.currentTimeMillis();
		
		if (sql.validate() == false) {
			return null;
		}
		
		List<Object[]> dataList = null;
		
		if (sql.isSelect()) {
			
			ResultSet rs = null;
			PreparedStatement stmt = null;

			try {
				stmt = this.createStatment(conn, sql);
				rs = stmt.executeQuery();
				
				dataList = getResultSet(rs);

			} catch (SQLException e) {
				throw e;
			} finally {
				try {
					if (rs != null && rs.isClosed() == false) {
						rs.close();
					}
					if (stmt != null && stmt.isClosed() == false) {
						stmt.close();
					}
				} catch (SQLException e) {
					throw e;
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
	 * 
	 * @throws SQLException 
	 */
	public boolean execute(Sql sql) throws SQLException {
		
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
			throw e;
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
	 * 执行存储过程
	 * 
	 * @param sql 需要执行的存储过程
	 * 
	 * @return 存储过程执行结果
	 * 
	 * @throws SQLException
	 */
	public Object[] call(Sql sql) throws SQLException {
		
		long start =  System.currentTimeMillis();
		
		if (sql.validate() == false) {
			return null;
		}
		
		List<Object> result = new ArrayList<Object>();
		
		Statement stmt = null;
		try {
			stmt = this.createStatment(conn, sql);
			if (stmt instanceof CallableStatement) {
				CallableStatement callStmt = (CallableStatement)stmt;
				callStmt.execute();
				List<Object> tmpResults = this.getProcedureOutValue(callStmt, sql);
				for (Object tmpResult : tmpResults) {
					if (tmpResult instanceof ResultSet) {
						result.add(getResultSet((ResultSet)tmpResult));
					} else {
						result.add(tmpResult);
					}
				}
			}
		} catch (SQLException e) {
			throw e;
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
		
		return result.toArray();
	}
	
	/**
	 * 批量执行SQL语句 
	 * 
	 * @param sqls SQL语句集合
	 * @param batchSize 每批次执行数量
	 * 
	 * @return 批量执行结果
	 * 
	 * @throws SQLException 
	 */
	public int[] execute(Iterable<String> sqls, int batchSize) throws SQLException {
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
			throw e;
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
	 * 
	 * @throws SQLException 
	 */
	public void commit() throws SQLException {
		conn.commit();
	}

	/**
	 * SQL事务: 回滚
	 * 
	 * @throws SQLException 
	 */
	public void rollback() throws SQLException {
		conn.rollback();
	}

	/**
	 * 关闭数据库连接
	 * 
	 * @throws SQLException 
	 */
	public void close() throws SQLException {
		conn.close();
	}
	
	/**
	 * 判断数据库连接是否关闭
	 * 
	 * @return 数据库是否关闭
	 * 
	 * @throws SQLException 
	 */
	public boolean isClosed() throws SQLException {
		if (conn != null) {
			return conn.isClosed();
		}
		return true;
	}

	
	private List<Object[]> getResultSet(ResultSet rs) throws SQLException {
		
		List<Object[]> dataList = new ArrayList<Object[]>();;
		
		ResultSetMetaData rsmd = rs.getMetaData();

		int columnCount = rsmd.getColumnCount();
		while (rs.next()) {
			Object[] row = new Object[columnCount];
			for (int i = 0; i < columnCount; i++) {
				row[i] = rs.getObject(i + 1);
			}
			dataList.add(row);
		}
		
		return dataList;
	}
}
