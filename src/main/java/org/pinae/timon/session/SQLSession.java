package org.pinae.timon.session;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.pinae.timon.mapper.MapMapper;
import org.pinae.timon.mapper.ObjectMapper;


/**
 * 数据库会话管理
 * 
 * @author Huiyugeng
 *
 */
public class SQLSession {

	private static Logger log = Logger.getLogger(SQLSession.class);

	private Connection conn = null;
	
	public SQLSession(Connection conn) {
		this.conn = conn;
	}
	
	/**
	 * 执行Select查询并返回第一个结果
	 * 
	 * @param sql Select语句
	 * 
	 * @return 执行结果
	 */
	public Object[] one(String sql) {
		List<Object[]> table = select(sql);
		if (table != null && table.size() > 0) {
			return table.get(0);
		}
		return null;
	}

	/**
	 * 执行Select查询
	 * 
	 * @param sql Select语句
	 * 
	 * @return 执行结果列表
	 */
	public List<Object[]> select(String sql) {
		if (StringUtils.isEmpty(sql)) {
			return null;
		} else {
			sql = sql.trim();
		}

		if (sql.toLowerCase().startsWith("select")) {

			ResultSet rs = null;
			Statement stmt = null;

			try {
				stmt = conn.createStatement();
				List<Object[]> table = new ArrayList<Object[]>();

				rs = stmt.executeQuery(sql);
				ResultSetMetaData rsmd = rs.getMetaData();

				int columnCount = rsmd.getColumnCount();
				while (rs.next()) {
					Object[] row = new Object[columnCount];
					for (int i = 0; i < columnCount; i++) {
						row[i] = rs.getObject(i + 1);
					}
					table.add(row);
				}

				return table;
			} catch (SQLException e) {
				log.error(String.format("%s : %s", sql, e.getMessage()));
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
		return null;
	}

	/**
	 * 执行计数查询
	 * 
	 * @param sql 需要计数的SQL语句（select语句）
	 * @return 查询计数结果
	 * 
	 */
	public long count(String sql) {
		if (StringUtils.isEmpty(sql)) {
			return 0;
		} else {
			sql = sql.trim();
		}
		//如果SQL语句中不包含count关键字，则构建一个计数SQL
		if (!StringUtils.containsIgnoreCase(sql, "count")) {
			sql = SQLBuilder.getCountSQL(sql);
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

	/**
	 * 执行SQL语句
	 * 
	 * @param sql 需要执行的SQL
	 * 
	 * @return 是否执行成功
	 */
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
			log.error(String.format("%s : %s", sql, e.getMessage()));
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

	/**
	 * 批量执行SQL语句
	 * 
	 * @param sqlList 需要执行的SQL列表
	 * 
	 * @return 是否执行成功
	 */
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
			log.error(String.format("%s", e.getMessage()));
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
	
	public SQLMetaData getMetaData() {
		if (conn != null) {
			return new SQLMetaData(conn);
		}
		return null;
	}

	public String[] getColumns(String sql) {
		SQLMetaData metadata = getMetaData();
		if (metadata != null) {
			return metadata.getColumnsBySQL(sql);
		}
		return null;
	}
	
	public List<Map<String, Object>> toMapList(List<Object[]> table, String columns[]) {
		if (table != null && columns != null) {
			MapMapper mapper = new MapMapper();
			return mapper.toMapList(table, columns);
		}
		return null;
	}
	
	public List<Map<String, Object>> toMapList(List<Object[]> table, String columns[], Map<String, Object> defaultMap) {
		if (table != null && columns != null) {
			MapMapper mapper = new MapMapper();
			if (defaultMap != null) {
				return mapper.toMapList(table, columns, defaultMap);
			} else {
				return mapper.toMapList(table, columns);
			}
		}
		return null;
	}
	
	public List<?> toObjectList(List<Object[]> table, String columns[], Class<?> clazz) {
		if (table != null && columns != null) {
			ObjectMapper mapper = new ObjectMapper();
			return (List<?>) mapper.toObjectList(table, columns, clazz);
		}
		return null;
	}
	
	/**
	 * 事务提交
	 * 
	 */
	public void commit() {
		try {
			conn.commit();
		} catch (SQLException e) {
			log.error(String.format("commit exception: exception=%s", e.getMessage()));
		}
	}
	
	/**
	 * 事务回滚
	 * 
	 */
	public void rollback() {
		try {
			conn.rollback();
		} catch (SQLException e) {
			log.error(String.format("rollback exception: exception=%s", e.getMessage()));
		}
	}

	/**
	 * 关闭数据库连接
	 */
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			log.error(String.format("close exception: exception=%s", e.getMessage()));
		}
	}
}
