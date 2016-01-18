package org.pinae.timon.session.datasource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.lang3.StringUtils;
import org.pinae.timon.util.ConfigMap;

public class JDBCDataSource implements DataSource {
	
	/* JDBC连接参数 */
	private ConfigMap<String, String> datasource = null;
	
	/**
	 * 构造函数
	 * 
	 * @param datasource JDBC配置信息
	 */
	public JDBCDataSource(ConfigMap<String, String> datasource) {
		this.datasource = datasource;
	}
	
	/**
	 * 获取数据库连接
	 * 
	 * @return 数据库连接
	 * 
	 * @throws IOException 数据库连接异常
	 */
	public Connection getConnection() throws IOException {
		Connection conn = null;
		try {
			if (this.datasource != null) {
				Class.forName(datasource.get("driver"));
				
				String url = datasource.get("url");
				String user = datasource.get("user");
				String password = datasource.get("password");
				if (StringUtils.isNoneBlank(user)) {
					conn = DriverManager.getConnection(url, user, password);
				} else {
					conn = DriverManager.getConnection(url);
				}
				conn.setAutoCommit(datasource.getBoolean("auto_commit", true));
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
		return conn;
	}
}
