package org.pinae.timon.session.defaults;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.pinae.timon.cache.Cache;
import org.pinae.timon.cache.CacheConfiguration;
import org.pinae.timon.cache.CacheException;
import org.pinae.timon.cache.CacheFactory;
import org.pinae.timon.session.SqlSession;
import org.pinae.timon.session.SqlSessionFactory;
import org.pinae.timon.session.datasource.BoneCPDataSource;
import org.pinae.timon.session.datasource.C3P0DataSource;
import org.pinae.timon.session.datasource.DBCPDataSource;
import org.pinae.timon.session.datasource.DataSource;
import org.pinae.timon.session.datasource.JDBCDataSource;
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
	private ConfigMap<String, String> sessionConfigMap = new ConfigMap<String, String>();

	public DefaultSqlSessionFactory() throws IOException {
		this(ClassLoaderUtils.getResourcePath("") + "database.properties");
	}

	public DefaultSqlSessionFactory(String filename) throws IOException {
		if (StringUtils.isBlank(filename)) {
			throw new NullPointerException("filename is NULL");
		}
		
		File file = new File(filename);
		if (file.exists() && file.isFile()) {
			this.sessionConfigMap = ConfigMap.load(file);
			createInstance();
		} else {
			throw new IOException("No such file : " + filename);
		}
	}

	public DefaultSqlSessionFactory(String type, String driver, String url, String user, String password) throws IOException {
		// 设置数据库数据源属性
		if (StringUtils.isAnyBlank(type, driver, url, user, password)) {
			throw new IOException("One or more datasource's properties is NULL");
		}
		this.sessionConfigMap.put("connection", type);
		this.sessionConfigMap.put("driver", driver);
		this.sessionConfigMap.put("url", url);
		this.sessionConfigMap.put("user", user);
		this.sessionConfigMap.put("password", password);

		createInstance();
	}

	public DefaultSqlSessionFactory(ConfigMap<String, String> datasource) throws IOException {
		createInstance();
	}

	private void createCache() throws IOException {
		String cacheConfigFile = ClassLoaderUtils.getResourcePath("") + "cache.properties";
		if (this.sessionConfigMap.containsKey("sql.cache.config")) {
			cacheConfigFile = this.sessionConfigMap.get("sql.cache.config");
		}
		
		ConfigMap<String, String> cacheConfigMap = ConfigMap.load(new File(cacheConfigFile));
		CacheConfiguration cacheConfig = CacheConfiguration.build(cacheConfigMap);
		
		try {
			if (cacheConfig != null) {
				
				String cacheName = this.toString();
				if (cacheConfigMap.containsKey("cache.name")) {
					cacheName = cacheConfigMap.get("cache.name");
					if (StringUtils.isBlank(cacheName)) {
						cacheName = this.toString();
					}
				}

				this.cache = CacheFactory.getInstance().createCache(cacheName, cacheConfig);
				
				logger.info(String.format("Cache create successful: adapter=%s, max_size=%d, expire=%d", 
						cacheConfigMap.get("cache.adapter"), cacheConfig.getMaxHeapSize(), cacheConfig.getExpire()));
			} else {
				logger.info("Cache is DISABLE");
			}
		} catch (CacheException e) {
			logger.error("Cache create error: " + e.getMessage());
		}
	}

	private void createInstance() throws IOException {
		// 构建数据源 (jdbc/c3p0/bonecp/dbcp)
		String type = this.sessionConfigMap.get("type");
		if (type != null) {
			if (type.equalsIgnoreCase("c3p0")) {
				this.datasource = new C3P0DataSource(sessionConfigMap);
			} else if (type.equalsIgnoreCase("bonecp")) {
				this.datasource = new BoneCPDataSource(sessionConfigMap);
			} else if (type.equalsIgnoreCase("dbcp")) {
				this.datasource = new DBCPDataSource(sessionConfigMap);
			} else if (type.equalsIgnoreCase("jdbc")) {
				this.datasource = new JDBCDataSource(sessionConfigMap);
			} else {
				throw new IOException(String.format("Unknow datasource type : %s", type));
			}
		} else {
			throw new NullPointerException("Datasource type is NULL");
		}
		
		// 构建缓存
		createCache();
	}

	public SqlSession getSession() throws IOException {
		return getSession(null);
	}

	public SqlSession getSession(ConnectionHandler handler) throws IOException {
		if (this.datasource != null) {
			Connection conn = this.datasource.getConnection();
			if (conn != null) {
				boolean autoCommit = this.sessionConfigMap.getBoolean("auto_commit", true);
				try {
					conn.setAutoCommit(autoCommit);
				} catch (SQLException e) {
					throw new IOException(e);
				}

				if (handler != null) {
					handler.handle(conn);
				}

				return new DefaultSqlSession(conn, this.cache, this.sessionConfigMap);
			}
		}
		return null;
	}

}
