package org.pinae.timon.session.executor;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import org.pinae.timon.sql.Sql;

public abstract class SqlStatement {
	
	protected PreparedStatement createStatment(Connection conn, Sql sql) throws SQLException {
		
		if (sql.isPreperStatement()) {
			PreparedStatement stmt = conn.prepareStatement(sql.getSql());
			Map<Integer, Object> prepareValues = sql.getPreperValues();
			
			Set<Integer> indexSet = prepareValues.keySet();
			for (Integer index : indexSet) {
				
				Object value = prepareValues.get(index);
				
				if (value instanceof String) {
					stmt.setString(index, value.toString());
				} else if (value instanceof Integer) {
					stmt.setInt(index, ((Integer)value).intValue());
				} else if (value instanceof Long) {
					stmt.setLong(index, ((Long)value).longValue());
				} else if (value instanceof Double) {
					stmt.setDouble(index, ((Double)value).doubleValue());
				} else if (value instanceof Float) {
					stmt.setFloat(index, ((Float)value).floatValue());
				} else if (value instanceof Short) {
					stmt.setShort(index, ((Short)value).shortValue());
				} else if (value instanceof Byte) {
					stmt.setByte(index, ((Byte)value).byteValue());
				} else if (value instanceof Boolean) {
					stmt.setBoolean(index, ((Boolean)value).booleanValue());
				} else if (value instanceof Date) {
					stmt.setDate(index, (Date)value);
				} else if (value instanceof Blob) {
					stmt.setBlob(index, (Blob)value);
				} else if (value instanceof Clob) {
					stmt.setClob(index, (Clob)value);
				} else if (value instanceof BigDecimal) {
					stmt.setBigDecimal(index, (BigDecimal)value);
				} else if (value instanceof Time) {
					stmt.setTime(index, (Time)value, Calendar.getInstance());
				} else if (value instanceof byte[]) {
					stmt.setBytes(index, (byte[])value);
				} else {
					stmt.setObject(index, value);
				}
			}
			
			return stmt;
		} else {
			return conn.prepareStatement(sql.getSql());
		}

	}
}
