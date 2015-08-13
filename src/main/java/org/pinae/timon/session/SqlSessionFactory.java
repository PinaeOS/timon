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
import org.pinae.timon.session.datasource.C3p0DataSource;
import org.pinae.timon.session.datasource.DBDataSource;
import org.pinae.timon.session.datasource.JdbcDataSource;
import org.pinae.timon.util.ClassLoaderUtils;
import org.pinae.timon.util.ConfigMap;

/**
 * 数据库会话工厂
 * 
 * @author Huiyugeng
 *
 */
public class SqlSessionFactory {
	
	private static Logger logger = Logger.getLogger(SqlSessionFactory.class);
	
	private DBDataSource datasource;
	
	private ConfigMap<String, String> config = new ConfigMap<String, String>();
	
	public SqlSessionFactory() throws IOException {
		this(ClassLoaderUtils.getResourcePath("") + "database.properties");
	}
	
	public SqlSessionFactory(String filename) throws IOException {
		this.config = getConfig(filename);
		
		createConnectionInstance();
	}
	
	public SqlSessionFactory(String type, String driver, String url, String user, String password) throws IOException {
		//设置数据库数据源属性
		if (StringUtils.isAnyBlank(type, driver, url, user, password)) {
			throw new IOException("One or more datasource's properties is NULL");
		}
		this.config.put("connection", type);
		this.config.put("driver", driver);
		this.config.put("url", url);
		this.config.put("user", user);
		this.config.put("password", password);
		
		createConnectionInstance();
	}
	
	public SqlSessionFactory(ConfigMap<String, String> datasource) throws IOException {
		createConnectionInstance();
	}
	
	private void createConnectionInstance() throws IOException {
		String type = config.get("type");
		if (type != null) {
			if (type.equalsIgnoreCase("c3p0")) {
				datasource = new C3p0DataSource(config);
			} else if (type.equalsIgnoreCase("jdbc")){
				datasource = new JdbcDataSource(config);
			} else {
				throw new IOException(String.format("Unknow datasource type : %s", type));
			}
			
			
		} else {
			throw new IOException("Datasource type is NULL");
		}
	}
	
	public SqlSession getSession() throws IOException {
		if (datasource != null) {
			Connection conn = datasource.getConnection();
			if (conn != null) {
				boolean autoCommit = config.getBoolean("auto_commit", true);
				try {
					conn.setAutoCommit(autoCommit);
				} catch (SQLException e) {
					throw new IOException(e);
				}
				
				return new SqlSession(getDBType(), conn);
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
		String driverKeywords[] = {DBType.MYSQL, DBType.ORACLE, DBType.SQLITE};
		//数据库驱动类		
		String driver = this.config.get("driver");
		
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
