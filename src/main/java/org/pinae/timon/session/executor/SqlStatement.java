package org.pinae.timon.session.executor;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.pinae.timon.io.SqlMapper.ProcedureObject.Out;
import org.pinae.timon.sql.Sql;

/**
 * SQL语句构建
 * 
 * @author Huiyugeng
 *
 */
public abstract class SqlStatement {

	/*
	 * 根据Sql对象和数据库连接构建PreparedStatement
	 */
	protected PreparedStatement createStatment(Connection conn, Sql sql) throws SQLException {

		if (sql == null) {
			throw new NullPointerException("Sql Object is NULL");
		}
		String sqlBody = sql.getSql();

		if (StringUtils.isNotEmpty(sqlBody)) {
			PreparedStatement stmt = null;
			if (sql.isProcedure()) {
				stmt = conn.prepareCall("{" + sqlBody + "}");
			} else if (sql.isPreperStatement()) {
				stmt = conn.prepareStatement(sqlBody);
			}
			
			if (stmt != null) {
				Map<Integer, Object> prepareValues = sql.getPreperValues();
	
				Set<Integer> indexSet = prepareValues.keySet();
				for (Integer index : indexSet) {
					Object value = prepareValues.get(index);
					if (index != null && value != null) {
						if (value instanceof String) {
							stmt.setString(index, value.toString());
						} else if (value instanceof Integer) {
							stmt.setInt(index, ((Integer) value).intValue());
						} else if (value instanceof Long) {
							stmt.setLong(index, ((Long) value).longValue());
						} else if (value instanceof Double) {
							stmt.setDouble(index, ((Double) value).doubleValue());
						} else if (value instanceof Float) {
							stmt.setFloat(index, ((Float) value).floatValue());
						} else if (value instanceof Short) {
							stmt.setShort(index, ((Short) value).shortValue());
						} else if (value instanceof Byte) {
							stmt.setByte(index, ((Byte) value).byteValue());
						} else if (value instanceof Boolean) {
							stmt.setBoolean(index, ((Boolean) value).booleanValue());
						} else if (value instanceof Date) {
							stmt.setDate(index, (Date) value);
						} else if (value instanceof Time) {
							stmt.setTime(index, (Time) value, Calendar.getInstance());
						} else if (value instanceof Blob) {
							stmt.setBlob(index, (Blob) value);
						} else if (value instanceof Clob) {
							stmt.setClob(index, (Clob) value);
						} else if (value instanceof BigDecimal) {
							stmt.setBigDecimal(index, (BigDecimal) value);
						} else if (value instanceof byte[]) {
							stmt.setBytes(index, (byte[]) value);
						} else if (value instanceof Out) {
							if (stmt instanceof CallableStatement) {
								int outType = getProcedureOutType((Out)value);
								if (outType != -999) {
									((CallableStatement)stmt).registerOutParameter(index, outType);
								}
							}
						} else {
							stmt.setObject(index, value);
						}
					}
				}
				return stmt;
			} else {
				return conn.prepareStatement(sqlBody);
			}
		} else {
			throw new NullPointerException("Sql Body is NULL");
		}
	}
	
	public int getProcedureOutType(Out out) {
		String outType = out.getType();
		if (outType != null) {
			if (outType.equalsIgnoreCase("BIT")) return Types.BIT;
			if (outType.equalsIgnoreCase("TINYINT")) return Types.TINYINT;
			if (outType.equalsIgnoreCase("SMALLIN")) return Types.SMALLINT;
			if (outType.equalsIgnoreCase("INT")) return Types.INTEGER;
			if (outType.equalsIgnoreCase("INTEGER")) return Types.INTEGER;
			if (outType.equalsIgnoreCase("BIGINT")) return Types.BIGINT;
			if (outType.equalsIgnoreCase("FLOAT")) return Types.FLOAT;
			if (outType.equalsIgnoreCase("REAL")) return Types.REAL;
			if (outType.equalsIgnoreCase("DOUBLE")) return Types.DOUBLE;
			if (outType.equalsIgnoreCase("NUMERIC")) return Types.NUMERIC;
			if (outType.equalsIgnoreCase("DECIMAL")) return Types.DECIMAL;
			if (outType.equalsIgnoreCase("CHAR")) return Types.CHAR;
			if (outType.equalsIgnoreCase("VARCHAR")) return Types.VARCHAR;
			if (outType.equalsIgnoreCase("LONGVARCHAR")) return Types.LONGVARCHAR;
			if (outType.equalsIgnoreCase("DATE")) return Types.DATE;
			if (outType.equalsIgnoreCase("TIME")) return Types.TIME;
			if (outType.equalsIgnoreCase("TIMESTAMP")) return Types.TIMESTAMP;
			if (outType.equalsIgnoreCase("BINARY")) return Types.BINARY;
			if (outType.equalsIgnoreCase("VARBINARY")) return Types.VARBINARY;
			if (outType.equalsIgnoreCase("LONGVARBINARY")) return Types.LONGVARBINARY;
			if (outType.equalsIgnoreCase("NULL")) return Types.NULL;
			if (outType.equalsIgnoreCase("OTHER")) return Types.OTHER;
			if (outType.equalsIgnoreCase("JAVA_OBJECT")) return Types.JAVA_OBJECT;
			if (outType.equalsIgnoreCase("DISTINCT")) return Types.DISTINCT;
			if (outType.equalsIgnoreCase("STRUCT")) return Types.STRUCT;
			if (outType.equalsIgnoreCase("ARRAY")) return Types.ARRAY;
			if (outType.equalsIgnoreCase("BLOB")) return Types.BLOB;
			if (outType.equalsIgnoreCase("CLOB")) return Types.CLOB;
			if (outType.equalsIgnoreCase("REF")) return Types.REF;
			if (outType.equalsIgnoreCase("DATALINK")) return Types.DATALINK;
			if (outType.equalsIgnoreCase("BOOLEAN")) return Types.BOOLEAN;
			if (outType.equalsIgnoreCase("ROWID")) return Types.ROWID;
			if (outType.equalsIgnoreCase("NCHAR")) return Types.NCHAR;
			if (outType.equalsIgnoreCase("NVARCHAR")) return Types.NVARCHAR;
			if (outType.equalsIgnoreCase("LONGNVARCHAR")) return Types.LONGNVARCHAR;
			if (outType.equalsIgnoreCase("NCLOB")) return Types.NCLOB;
			if (outType.equalsIgnoreCase("SQLXML")) return Types.SQLXML;
			if (outType.equalsIgnoreCase("RESULTSET")) return -99;
		}
		return -999;
	}
	
	public List<Object> getProcedureOutValue(CallableStatement stmt, Sql sql) throws SQLException {
		if (stmt != null) {
			List<Object> result = new ArrayList<Object>();
			
			Map<Integer, Object> prepareValues = sql.getPreperValues();

			Set<Integer> indexSet = prepareValues.keySet();
			for (Integer index : indexSet) {

				Object value = prepareValues.get(index);
				if (value instanceof Out) {
					int outType = getProcedureOutType((Out)value);
					if (outType == Types.INTEGER) {
						result.add(stmt.getInt(index));
					} else if (outType == Types.TINYINT || outType == Types.SMALLINT) {
						result.add(stmt.getShort(index));
					} else if (outType == Types.BIGINT) {
						result.add(stmt.getLong(index));
					} else if (outType == Types.FLOAT) {
						result.add(stmt.getFloat(index));
					} else if (outType ==  Types.REAL || outType ==   Types.DOUBLE || outType ==   Types.NUMERIC || outType ==  Types.DECIMAL) {
						result.add(stmt.getDouble(index));
					} else if (outType == Types.CHAR || outType == Types.VARCHAR || outType == Types.LONGVARCHAR) {
						result.add(stmt.getString(index));
					} else if (outType == Types.NCHAR || outType == Types.NVARCHAR || outType == Types.LONGNVARCHAR) {
						result.add(stmt.getNString(index));
					} else if (outType == Types.DATE) {
						result.add(stmt.getDate(index));
					} else if (outType == Types.TIME) {
						result.add(stmt.getTime(index));
					} else if (outType == Types.TIMESTAMP) {
						result.add(stmt.getTimestamp(index));
					} else if (outType == Types.BOOLEAN) {
						result.add(stmt.getBoolean(index));
					} else if (outType == Types.BLOB) {
						result.add(stmt.getBlob(index));
					} else if (outType == Types.CLOB) {
						result.add(stmt.getClob(index));
					} else if (outType == Types.ARRAY) {
						result.add(stmt.getArray(index));
					} else if (outType == Types.NCLOB) {
						result.add(stmt.getNClob(index));
					} else if (outType == Types.SQLXML) {
						result.add(stmt.getSQLXML(index));
					} else if (outType == Types.JAVA_OBJECT) {
						result.add(stmt.getObject(index));
					} else if (outType == Types.ROWID) {
						result.add(stmt.getRowId(index));
					} else if (outType == Types.REF) {
						result.add(stmt.getRef(index));
					} else if (outType == Types.NULL) {
						result.add(null);
					} else if (outType == -99) {
						result.add(stmt.getResultSet());
					}
				}
			}
			
			return result;
		}
		return null;
	}

}


