package org.pinae.timon.session.datasource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.pinae.timon.util.ConfigMap;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

/**
 * 基于BoneCP的数据库连接池管理
 * 
 * @author Huiyugeng
 * 
 */
public class BoneCPDataSource implements DataSource {
	
	private BoneCP datasource = null;
	
	public BoneCPDataSource(ConfigMap<String, String> datasource) throws IOException {
		try {
			Class.forName(datasource.get("driver"));
		} catch (ClassNotFoundException e) {
			throw new IOException("Load jdbc driver fail" + datasource.get("driver"));
		}
		
		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl(datasource.get("url"));
		config.setUsername(datasource.get("user")); 
		config.setPassword(datasource.get("password"));
		
		config.setMinConnectionsPerPartition(datasource.getInteger("min_pool_size", 1));
		config.setMaxConnectionsPerPartition(datasource.getInteger("max_pool_size", 10));
		config.setPartitionCount(datasource.getInteger("partition_size", 1));
		
		try {
			this.datasource = new BoneCP(config);
		} catch (SQLException e) {
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
		if (this.datasource != null) {
			try {
				conn = this.datasource.getConnection();
			} catch (SQLException e) {
				throw new IOException(e);
			}
		}
		return conn;
	}

}
