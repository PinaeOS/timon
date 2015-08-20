package org.pinae.timon.session.datasource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;
import org.pinae.timon.util.ConfigMap;

public class JdbcDataSource implements DBDataSource {
	
	private static Logger log = Logger.getLogger(JdbcDataSource.class);
	
	/* JDBC连接参数 */
	private ConfigMap<String, String> datasource = null;
	
	/**
	 * 构造函数
	 * 
	 * @param filename JDBC配置文件名
	 */
	public JdbcDataSource(ConfigMap<String, String> datasource) {
		this.datasource = datasource;
	}
	
	/**
	 * 获取数据库连接
	 * 
	 * @return 数据库连接
	 * 
	 * @throws IOException 
	 */
	public Connection getConnection() throws IOException {
		Connection conn = null;
		try {
			if (this.datasource != null) {
				Class.forName(datasource.get("driver"));
				conn = DriverManager.getConnection(datasource.get("url"), datasource.get("user"), datasource.get("password"));
				conn.setAutoCommit(datasource.getBoolean("auto_commit", true));
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
		return conn;
	}
}