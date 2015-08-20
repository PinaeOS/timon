package org.pinae.timon.session.executor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


/**
 * 数据库访问执行器
 * 
 * @author huiyugeng
 *
 */
public class SqlExecutor {
	
	private static Logger log = Logger.getLogger(SqlExecutor.class);
	
	private Connection conn = null;
	
	public SqlExecutor(Connection conn) {
		this.conn = conn;
	}

	public List<Object[]> select(String sql) {
		if (StringUtils.isEmpty(sql)) {
			return null;
		} else {
			sql = sql.trim();
		}
		
		List<Object[]> dataList = null;
		
		if (sql.toLowerCase().startsWith("select")) {

			dataList = new ArrayList<Object[]>();
			
			ResultSet rs = null;
			Statement stmt = null;

			try {
				stmt = conn.createStatement();

				rs = stmt.executeQuery(sql);
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
				log.error(String.format("select Exception: exception=%s; sql=%s", e.getMessage(), sql));
			} finally {
				try {
					if (rs != null && rs.isClosed() == false) {
						rs.close();
					}
					if (stmt != null && stmt.isClosed() == false) {
						stmt.close();
					}
				} catch (SQLException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return dataList;
	}
	



	public boolean execute(String sql) {
		if (StringUtils.isEmpty(sql)) {
			return false;
		} else {
			sql = sql.trim();
		}

		boolean result = false;

		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.execute(sql);
			result = true;
		} catch (SQLException e) {
			log.error(String.format("execute Exception: exception=%s; sql=%s", e.getMessage(), sql));
		} finally {
			try {
				if (stmt != null && stmt.isClosed() == false) {
					stmt.close();
				}
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}

		return result;
	}

	public boolean execute(List<String> sqlList) {

		if (sqlList == null || sqlList.size() == 0) {
			return false;
		}

		boolean result = false;

		Statement stmt = null;

		try {
			stmt = conn.createStatement();
			for (String sql : sqlList) {
				if (StringUtils.isNotEmpty(sql)) {
					sql = sql.trim();
					stmt.addBatch(sql);
				}
			}
			stmt.executeBatch();
			result = true;
		} catch (SQLException e) {
			log.error(String.format("execute Exception: exception=%s", e.getMessage()));
		} finally {
			try {
				if (stmt != null && stmt.isClosed() == false) {
					stmt.close();
				}
			} catch (SQLException e) {
				log.error(e.getMessage());
			}
		}

		return result;
	}
	
	public void commit() {
		try {
			conn.commit();
		} catch (SQLException e) {
			log.error(String.format("commit Exception: exception=%s", e.getMessage()));
		}
	}

	public void rollback() {
		try {
			conn.rollback();
		} catch (SQLException e) {
			log.error(String.format("rollback Exception: exception=%s", e.getMessage()));
		}
	}

	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			log.error(String.format("close Exception: exception=%s", e.getMessage()));
		}
	}
	
	public boolean isClosed() {
		try {
			if (conn != null) {
				return conn.isClosed();
			}
		} catch (SQLException e) {
			log.error(String.format("isClosed Exception: exception=%s", e.getMessage()));
		}
		return true;
	}

}
