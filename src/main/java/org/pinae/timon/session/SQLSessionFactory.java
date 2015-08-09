package org.pinae.timon.session;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.pinae.timon.session.datasource.C3P0Connection;
import org.pinae.timon.session.datasource.DBConnection;
import org.pinae.timon.session.datasource.JDBCConnection;
import org.pinae.timon.util.ClassLoaderUtils;
import org.pinae.timon.util.ConfigMap;

/**
 * 数据库会话工厂
 * 
 * @author Huiyugeng
 *
 */
public class SQLSessionFactory {
	
	private static Logger logger = Logger.getLogger(SQLSessionFactory.class);
	
	private DBConnection dbConn;
	
	private ConfigMap<String, String> datasource = new ConfigMap<String, String>();
	
	public SQLSessionFactory() throws IOException {
		this(ClassLoaderUtils.getResourcePath("") + "database.properties");
	}
	
	public SQLSessionFactory(String filename) throws IOException {
		this.datasource = getConfig(filename);
		
		createConnectionInstance();
	}
	
	public SQLSessionFactory(String type, String driver, String url, String user, String password) throws IOException {
		//设置数据库数据源属性
		if (StringUtils.isAnyBlank(type, driver, url, user, password)) {
			throw new IOException("One or more datasource's properties is NULL");
		}
		this.datasource.put("connection", type);
		this.datasource.put("driver", driver);
		this.datasource.put("url", url);
		this.datasource.put("user", user);
		this.datasource.put("password", password);
		
		createConnectionInstance();
	}
	
	public SQLSessionFactory(ConfigMap<String, String> datasource) throws IOException {
		createConnectionInstance();
	}
	
	private void createConnectionInstance() throws IOException {
		String type = datasource.get("type");
		if (type != null) {
			if (type.equalsIgnoreCase("c3p0")) {
				dbConn = new C3P0Connection(datasource);
			} else if (type.equalsIgnoreCase("jdbc")){
				dbConn = new JDBCConnection(datasource);
			} else {
				throw new IOException(String.format("Unknow datasource type : %s", type));
			}
			
			
		} else {
			throw new IOException("Datasource type is NULL");
		}
	}
	
	public SQLSession getSession() throws IOException {
		if (dbConn != null) {
			Connection conn = dbConn.getConnection();
			if (conn != null) {
				boolean autoCommit = datasource.getBoolean("auto_commit", true);
				try {
					conn.setAutoCommit(autoCommit);
				} catch (SQLException e) {
					throw new IOException(e);
				}
				
				return new SQLSession(getDBType(), conn);
			}
		}
		return null;
	}
	
	private ConfigMap<String, String> getConfig(String filename) throws IOException {
		
		ConfigMap<String, String> configMap = new ConfigMap<String, String>();
		
		try {
			Properties properties = new Properties();

			InputStream in = new BufferedInputStream(new FileInputStream(filename));
			properties.load(in);

			Set<Object> propKeySet = properties.keySet();
			for (Object propKey : propKeySet) {
				if (propKeySet != null) {
					String key = propKey.toString();
					String value = properties.getProperty(key);
					configMap.put(key, value);
				}
			}

			IOUtils.closeQuietly(in);

		} catch (Exception e) {
			throw new IOException("Read XML config file ERROR :" + e.getMessage());
		}
		
		return configMap;
	}
	
	private String getDBType() {
		//数据库驱动关键字
		String driverKeywords[] = {"mysql", "oracle", "sqlite"};
		//数据库驱动类		
		String driver = this.datasource.get("driver");
		
		String dbType = null;
		if (StringUtils.isNotBlank(driver)) {
			for (String keyword : driverKeywords) {
				if (driver.contains(keyword)) {
					dbType = keyword;
				}
			}
		}
		
		return dbType;
	}
	
}
