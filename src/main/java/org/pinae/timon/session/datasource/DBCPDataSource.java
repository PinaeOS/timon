package org.pinae.timon.session.datasource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.pinae.timon.util.ConfigMap;

/**
 * 基于DBCP的数据库连接池管理
 * 
 * @author Huiyugeng
 * 
 */
public class DBCPDataSource implements DataSource {

	private BasicDataSource datasource;

	public DBCPDataSource(ConfigMap<String, String> datasource) throws IOException {
		try {
			this.datasource = new BasicDataSource();

			this.datasource.setDriverClassName(datasource.get("driver"));
			this.datasource.setUrl(datasource.get("url"));
			this.datasource.setUsername(datasource.get("user"));
			this.datasource.setPassword(datasource.get("password"));
			
			this.datasource.setInitialSize(datasource.getInteger("init_pool_size", 5));
			this.datasource.setMinIdle(datasource.getInteger("min_pool_size", 1));
			this.datasource.setMaxIdle(datasource.getInteger("max_pool_size", 10));
			this.datasource.setMaxWaitMillis(datasource.getInteger("max_idle_time", 60) * 1000);

		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
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
