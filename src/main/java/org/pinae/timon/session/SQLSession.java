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
import org.pinae.timon.mapper.AnnotationMapper;
import org.pinae.timon.mapper.MapMapper;
import org.pinae.timon.mapper.Mapper;
import org.pinae.timon.mapper.ObjectMapper;
import org.pinae.timon.mapper.annotation.Entity;

/**
 * 数据库会话管理
 * 
 * @author Huiyugeng
 *
 */
public class SQLSession {
	private static Logger log = Logger.getLogger(SQLSession.class);

	private String dbType = null;
	private Connection conn = null;
	
	public SQLSession(String dbType, Connection conn) {
		this.dbType = dbType;
		this.conn = conn;
	}
	
	public Object[] one(String sql) {
		List<Object[]> table = select(sql);
		if (table != null && table.size() > 0) {
			return table.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T one(String sql, Class<T> clazz) {
		List<?> table = select(sql, clazz);
		if (table != null && table.size() > 0) {
			return (T) table.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T one(String sql, String[] columns, Class<T> clazz) {
		List<?> table = select(sql, columns, clazz);
		if (table != null && table.size() > 0) {
			return (T) table.get(0);
		}
		return null;
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
	
	public List<?> select(String sql, String[] columns, Class<?> clazz) {
		List<Object[]> dataList = select(sql);
		if (clazz == null) {
			clazz = Map.class;
		}
		
		Mapper mapper = null;
		if (clazz.equals(Map.class)) {
			mapper = new MapMapper();
		} else {
			if (clazz.isAnnotationPresent(Entity.class)) {
				mapper = new AnnotationMapper(clazz);
			} else {
				mapper = new ObjectMapper(clazz);
			}
		}
		
		List<?> table = null;
		if (mapper != null) {
			table = mapper.toList(dataList, columns);
		}
		
		return table;
	}
	
	public List<?> select(String sql, Class<?> clazz) {
		List<?> table = null;
		
		String columns[] = getColumns(sql);
		if (columns != null) {
			table = select(sql, columns, clazz);
		}
		return table;
	}
	

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
	
	public Connection getConnection() {
		return this.conn;
	}
	
	public SQLMetadata getMetadata() {
		if (conn != null) {
			return new SQLMetadata(conn);
		}
		return null;
	}

	public String[] getColumns(String sql) {
		SQLMetadata metadata = getMetadata();
		if (metadata != null) {
			return metadata.getColumnsBySQL(sql);
		}
		return null;
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

	public String getDbType() {
		return dbType;
	}

}
