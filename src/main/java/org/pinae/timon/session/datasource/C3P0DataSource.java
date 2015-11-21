package org.pinae.timon.session.datasource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.pinae.timon.util.ConfigMap;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 基于C3P0的数据库连接池管理
 * 
 * @author Huiyugeng
 * 
 */
public class C3P0DataSource implements DataSource {
	
	private ComboPooledDataSource datasource;

	public C3P0DataSource(ConfigMap<String, String> datasource) throws IOException {
		try {

			this.datasource = new ComboPooledDataSource();
			this.datasource.setJdbcUrl(datasource.get("url"));
			this.datasource.setDriverClass(datasource.get("driver"));
			this.datasource.setUser(datasource.get("user"));
			this.datasource.setPassword(datasource.get("password"));
			
			this.datasource.setInitialPoolSize(datasource.getInteger("init_pool_size", 2));
			this.datasource.setMinPoolSize(datasource.getInteger("min_pool_size", 1));
			this.datasource.setMaxPoolSize(datasource.getInteger("max_pool_size", 10));
			this.datasource.setMaxStatements(datasource.getInteger("max_statement", 50));
			this.datasource.setMaxIdleTime(datasource.getInteger("max_idle_time", 60));

		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	

	/**
	 * 获取数据库连接
	 * 
	 * @return 数据库连接
	 */
	public Connection getConnection() throws IOException {
		Connection conn = null;
		try {
			conn = this.datasource.getConnection();
		} catch (SQLException e) {
			throw new IOException(e);
		}
		return conn;
	}

}
