package org.pinae.timon.session.defaults;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.pinae.timon.cache.Cache;
import org.pinae.timon.cache.CacheConfiguration;
import org.pinae.timon.cache.CacheException;
import org.pinae.timon.cache.CacheFactory;
import org.pinae.timon.session.DBType;
import org.pinae.timon.session.SqlSession;
import org.pinae.timon.session.SqlSessionFactory;
import org.pinae.timon.session.datasource.C3p0DataSource;
import org.pinae.timon.session.datasource.DataSource;
import org.pinae.timon.session.datasource.JdbcDataSource;
import org.pinae.timon.session.handle.ConnectionHandler;
import org.pinae.timon.util.ClassLoaderUtils;
import org.pinae.timon.util.ConfigMap;

/**
 * 数据库会话工厂
 * 
 * @author Huiyugeng
 *
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {
	
	private static Logger logger = Logger.getLogger(DefaultSqlSessionFactory.class);
	
	/**
	 * 会话数据源
	 */
	private DataSource datasource;

	/**
	 * 缓存
	 */
	private Cache cache;

	/**
	 * Session配置信息
	 */
	private ConfigMap<String, String> config = new ConfigMap<String, String>();

	public DefaultSqlSessionFactory() throws IOException {
		this(ClassLoaderUtils.getResourcePath("") + "database.properties");
	}

	public DefaultSqlSessionFactory(String filename) throws IOException {
		this.config = ConfigMap.getConfig(filename);

		createInstance();
	}

	public DefaultSqlSessionFactory(String type, String driver, String url, String user, String password) throws IOException {
		// 设置数据库数据源属性
		if (StringUtils.isAnyBlank(type, driver, url, user, password)) {
			throw new IOException("One or more datasource's properties is NULL");
		}
		this.config.put("connection", type);
		this.config.put("driver", driver);
		this.config.put("url", url);
		this.config.put("user", user);
		this.config.put("password", password);

		createInstance();
	}

	public DefaultSqlSessionFactory(ConfigMap<String, String> datasource) throws IOException {
		createInstance();
	}

	private void createCache() {
		CacheConfiguration cacheCofig = CacheConfiguration.getConfig(config);
		try {
			this.cache = CacheFactory.getInstance().createCache(this.toString(), cacheCofig);
		} catch (CacheException e) {
			logger.error("create error : " + e.getMessage());
		}
	}

	private void createInstance() throws IOException {
		String type = config.get("type");
		if (type != null) {
			if (type.equalsIgnoreCase("c3p0")) {
				datasource = new C3p0DataSource(config);
			} else if (type.equalsIgnoreCase("jdbc")) {
				datasource = new JdbcDataSource(config);
			} else {
				throw new IOException(String.format("Unknow datasource type : %s", type));
			}
		} else {
			throw new IOException("Datasource type is NULL");
		}
		
		// 构建缓存
		createCache();
	}

	public SqlSession getSession() throws IOException {
		return getSession(null);
	}

	public SqlSession getSession(ConnectionHandler handler) throws IOException {
		if (datasource != null) {
			Connection conn = datasource.getConnection();
			if (conn != null) {
				boolean autoCommit = config.getBoolean("auto_commit", true);
				try {
					conn.setAutoCommit(autoCommit);
				} catch (SQLException e) {
					throw new IOException(e);
				}

				if (handler != null) {
					handler.handle(conn);
				}

				return new DefaultSqlSession(getDBType(), conn, this.cache);
			}
		}
		return null;
	}

	private String getDBType() {
		// 数据库驱动关键字
		String driverKeywords[] = { DBType.MYSQL, DBType.ORACLE, DBType.SQLITE };
		// 数据库驱动类
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
