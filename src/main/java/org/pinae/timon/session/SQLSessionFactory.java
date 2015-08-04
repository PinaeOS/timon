package org.pinae.timon.session;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
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
	
	private DBConnection connection;
	
	public SQLSessionFactory() {
		this(ClassLoaderUtils.getResourcePath("") + "database.properties");
	}
	
	public SQLSessionFactory(String filename) {
		ConfigMap<String, String> datasource = getConfig(filename);
		
		createConnectionInstance(datasource);
	}
	
	public SQLSessionFactory(String type, String driver, String url, String user, String password) {
		ConfigMap<String, String> datasource = new ConfigMap<String, String>();
		datasource.put("connection", type);
		datasource.put("driver", driver);
		datasource.put("url", url);
		datasource.put("user", user);
		datasource.put("password", password);
		
		createConnectionInstance(datasource);
	}
	
	public SQLSessionFactory(ConfigMap	<String, String> datasource) {
		createConnectionInstance(datasource);
	}
	
	private void createConnectionInstance(ConfigMap<String, String> datasource) {
		String type = datasource.get("type");
		if (type != null && type.equalsIgnoreCase("c3p0")) {
			connection = new C3P0Connection(datasource);
		} else {
			connection = new JDBCConnection(datasource);
		}
	}
	
	public SQLSession getSession() {
		if (connection != null) {
			Connection conn = connection.getConnection();
			if (conn != null) {
				return new SQLSession(conn);
			}
		}
		return null;
	}
	
	private ConfigMap<String, String> getConfig(String filename) {
		
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
			logger.error("Connection Database Error :" + e.getMessage());
		}
		
		return configMap;
	}
	
}
