package org.pinae.timon.session.datasource;

import org.apache.commons.lang3.StringUtils;

/**
 * 数据库驱动集合
 * 
 * @author Huiyugeng
 *
 */
public class DataSourceDriver {
	
	/* Oracle 数据库驱动 */
	public static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
	
	/* DB2 数据库驱动 */
	public static final String DB2_DRIVER = "com.ibm.db2.jcc.DB2Driver";
	
	/* MySQL 数据库驱动 */
	public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	
	/* PostgreSQL 数据库驱动  */
	public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
	
	/* SqlServer 数据库驱动 */
	public static final String MSSQL_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	
	/* Sqlite 数据库驱动 */
	public static final String SQLITE_DIRVER = "org.sqlite.JDBC";
	
	public static String getDriver(String dbType) {
		if (StringUtils.isEmpty(dbType)) {
			return null;
		}
		dbType = dbType.toUpperCase();
		if ("ORACLE".equals(dbType)) {
			return ORACLE_DRIVER;
		} else if ("DB2".equals(dbType)) {
			return DB2_DRIVER;
		} else if ("MYSQL".equals(dbType)) {
			return MYSQL_DRIVER;
		} else if ("POSTGRESQL".equals(dbType)) {
			return POSTGRESQL_DRIVER;
		} else if ("MSSQL".equals(dbType)) {
			return MSSQL_DRIVER;
		} else if ("SQLITE".equals(dbType)) {
			return SQLITE_DIRVER;
		}
		return null;
	}
	
	public static String getDriverByUrl(String url) {
		if (StringUtils.isEmpty(url)) {
			return null;
		}
		url = url.trim();
		if (url.startsWith("jdbc:oracle:thin")) {
			return ORACLE_DRIVER;
		} else if (url.startsWith("jdbc:db2")) {
			return DB2_DRIVER;
		} else if (url.startsWith("jdbc:mysql")) {
			return MYSQL_DRIVER;
		} else if (url.startsWith("jdbc:postgresql")) {
			return POSTGRESQL_DRIVER;
		} else if (url.startsWith("jdbc:sqlserver")) {
			return MSSQL_DRIVER;
		} else if (url.startsWith("jdbc:sqlite")) {
			return SQLITE_DIRVER;
		}
		return null;
	}
}
