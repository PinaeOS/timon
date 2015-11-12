package org.pinae.timon.session.executor;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class SqlMetadata {

	private static Logger logger = Logger.getLogger(SqlMetadata.class);

	private Connection conn = null;
	
	public SqlMetadata(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 通过SQL语句获取元数据信息
	 * 
	 * @param sql SQL语句
	 * 
	 * @return 元数据信息
	 */
	public List<Map<String, String>> getMetadataBySql(String sql) {
		
		List<Map<String, String>> table = new ArrayList<Map<String, String>>();
		
		try {

			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsme = rs.getMetaData();

			int columnCount = rsme.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				
				Map<String, String> field = new HashMap<String, String>();

				field.put("NAME", rsme.getColumnName(i)); // 字段名称
				field.put("TYPE", rsme.getColumnTypeName(i)); //字段类型名称(例如：VACHAR2)
				field.put("SIZE", Integer.toString(rsme.getPrecision(i))); //字段长度
				field.put("NULLABLE", rsme.isNullable(i) == ResultSetMetaData.columnNullable ? "YES" : "NO"); //是否为空
				field.put("REMARK", rsme.getColumnLabel(i)); //字段注释
				
				table.add(field);
			}

		} catch (Exception e) {
			logger.error(String.format("getMetadataBySQL Exception: exception=%s", e.getMessage()));
		}
		return table;
	}
	
	/**
	 * 根据SQL语句获取SQL返回的列名
	 * 
	 * @param sql SQL语句
	 * 
	 * @return 列名
	 */
	public String[] getColumnsBySql(String sql) {
		String columns[] = null;
		
		List<Map<String, String>> table = getMetadataBySql(sql);
		if (table != null) {
			columns = new String[table.size()];
			for (int i = 0 ; i < table.size() ; i++) {
				Map<String, String> row = table.get(i);
				if (row.containsKey("NAME") && row.get("NAME") != null) {
					columns[i] = row.get("NAME");
				}
			}
		}
		
		return columns;
	}

	/**
	 * 通过表名获取元数据信息
	 * 
	 * @param schema 模式名称
	 * @param tableName 表名称
	 * 
	 * @return 元数据信息
	 */
	public List<Map<String, String>> getMetadataByTable(String schema, String tableName) {
		
		List<Map<String, String>> table = new ArrayList<Map<String, String>>();
		
		try {
			DatabaseMetaData metadata = conn.getMetaData();
			ResultSet rs = metadata.getColumns(null, schema, tableName, null);
			
			while (rs.next()) {
				
				Map<String, String> field = new HashMap<String, String>();
				
				field.put("NAME",  rs.getString("COLUMN_NAME"));// 字段名称
				field.put("TYPE", rs.getString("TYPE_NAME")); //字段类型名称(例如：VACHAR2)
				field.put("SIZE", Integer.toString(rs.getInt("COLUMN_SIZE"))); //字段长度
				field.put("NULLABLE", rs.getString("IS_NULLABLE")); //是否为空
				field.put("REMARK", rs.getString("REMARKS")); //字段注释
				
				table.add(field);
				
			}
		} catch (Exception e) {
			logger.error(String.format("getMetadataByTable Exception: exception=%s", e.getMessage()));
		}
		return table;
	}
	
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error(String.format("close Exception: exception=%s", e.getMessage()));
			}
		}
	}
}