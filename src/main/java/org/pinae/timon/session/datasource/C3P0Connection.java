package org.pinae.timon.session.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.pinae.timon.util.ConfigMap;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 数据库连接池管理
 * 
 * @author Huiyugeng
 * 
 */
public class C3P0Connection implements DBConnection {

	private static Logger log = Logger.getLogger(C3P0Connection.class);
	
	private ComboPooledDataSource datasource;

	public C3P0Connection(ConfigMap<String, String> datasource) {
		try {

			this.datasource = new ComboPooledDataSource();
			this.datasource.setJdbcUrl(datasource.get("url"));
			this.datasource.setDriverClass(datasource.get("driver"));
			this.datasource.setUser(datasource.get("user"));
			this.datasource.setPassword(datasource.get("password"));
			this.datasource.setInitialPoolSize(2);
			this.datasource.setMinPoolSize(1);
			this.datasource.setMaxPoolSize(10);
			this.datasource.setMaxStatements(50);
			this.datasource.setMaxIdleTime(60);

		} catch (Exception e) {
			log.error("Connection Database Error :" + e.getMessage());
		}
	}
	

	/**
	 * 获取数据库连接
	 * 
	 * @return 数据库连接
	 */
	public Connection getConnection() {
		Connection conn = null;
		try {
			conn = datasource.getConnection();
		} catch (SQLException e) {
			log.error("Connection Database Error :" + e.getMessage());
		}
		return conn;
	}

}
