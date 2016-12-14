package org.pinae.timon.session.defaults;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

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
	private ConfigMap<String, String> configMap = new ConfigMap<String, String>();

	public DefaultSqlSessionFactory() throws IOException {
		this(ClassLoaderUtils.getResourcePath("") + "database.properties");
	}

	/**
	 * 构造函数
	 * 
	 * @param file 配置文件
	 * 
	 * @throws IOException 配置文件IO异常
	 */
	public DefaultSqlSessionFactory(File file) throws IOException {
		if (file == null) {
			throw new NullPointerException("file is NULL");
		}
		this.configMap = ConfigMap.load(file);
		createInstance();
	}

	/**
	 * 构造函数
	 * 
	 * @param filename 配置文件路径
	 * 
	 * @throws IOException 配置文件IO异常
	 */
	public DefaultSqlSessionFactory(String filename) throws IOException {
		if (StringUtils.isBlank(filename)) {
			throw new NullPointerException("filename is NULL");
		}

		filename = getFilePath(filename);
		if (filename != null) {
			this.configMap = ConfigMap.load(filename);
			createInstance();
		} else {
			throw new IOException("No such file : " + filename);
		}
	}

	/**
	 * 构造函数
	 * 
	 * @param type 数据库连接方式 jdbc/c3p0/bonecp/dbcp
	 * @param driver 数据库驱动
	 * @param url 数据库连接地址
	 * @param user 数据库连接用户名
	 * @param password 数据库连接密码
	 * 
	 * @throws IOException 配置信息IO异常
	 */
	public DefaultSqlSessionFactory(String type, String driver, String url, String user, String password) throws IOException {
		// 设置数据库数据源属性
		if (StringUtils.isAnyBlank(type, driver, url, user, password)) {
			throw new IOException("One or more datasource's properties is NULL");
		}
		this.configMap.put("type", type);
		this.configMap.put("driver", driver);
		this.configMap.put("url", url);
		this.configMap.put("user", user);
		this.configMap.put("password", password);

		createInstance();
	}

	/**
	 * 构造函数
	 * 
	 * @param datasource 数据源配置信息
	 * 
	 * @throws IOException 配置信息IO异常
	 */
	public DefaultSqlSessionFactory(Map<String, String> datasource) throws IOException {
		this.configMap = new ConfigMap<String, String>(datasource);
		createInstance();
	}
	
	/**
	 * 构建函数
	 * 
	 * @param connection 数据库连接
	 * 
	 * @throws IOException 数据库连接异常
	 */
	public DefaultSqlSessionFactory(final Connection connection) throws IOException {
		if (connection != null) {
			this.datasource = new DataSource() {
				public Connection getConnection() throws IOException {
					return connection;
				}
			};
		}
	}

	private void createCache() throws IOException {

		String path = ClassLoaderUtils.getResourcePath("");
		String cacheConfigFile = path + "cache.properties";

		if (this.configMap.containsKey("sql.cache.config")) {
			cacheConfigFile = this.configMap.get("sql.cache.config");
		}

		cacheConfigFile = getFilePath(cacheConfigFile);
		if (cacheConfigFile != null) {
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

					logger.info(String.format("Cache create successful: adapter=%s, max_size=%d, expire=%d", cacheConfigMap.get("cache.adapter"),
							cacheConfig.getMaxHeapSize(), cacheConfig.getExpire()));
				} else {
					logger.info("Cache is DISABLE");
				}
			} catch (CacheException e) {
				logger.error("Cache create error: " + e.getMessage());
			}
		} else {
			logger.info("No cache properties file, Cache is DISABLE");
		}

	}

	private void createInstance() throws IOException {
		// 构建数据源 (jdbc/c3p0/bonecp/dbcp)
		String type = this.configMap.get("type");
		if (type != null) {
			if (type.equalsIgnoreCase("c3p0")) {
				this.datasource = new C3P0DataSource(configMap);
			} else if (type.equalsIgnoreCase("bonecp")) {
				this.datasource = new BoneCPDataSource(configMap);
			} else if (type.equalsIgnoreCase("dbcp")) {
				this.datasource = new DBCPDataSource(configMap);
			} else if (type.equalsIgnoreCase("jdbc")) {
				this.datasource = new JDBCDataSource(configMap);
			} else {
				throw new IOException(String.format("Unknow datasource type : %s", type));
			}
		} else {
			throw new NullPointerException("Datasource type is NULL");
		}

		// 构建缓存
		createCache();
	}

	public boolean testConnection() {
		if (this.datasource != null) {
			try {
				Connection conn = this.datasource.getConnection();
				if (conn != null) {
					conn.close();
					return true;
				}
			} catch (Exception e) {
				
			}
		}
		return false;
	}

	public SqlSession getSession() throws IOException {
		return getSession(null);
	}

	public SqlSession getSession(ConnectionHandler handler) throws IOException {
		if (this.datasource != null) {
			Connection conn = this.datasource.getConnection();
			if (conn != null) {
				boolean autoCommit = this.configMap.getBoolean("auto_commit", true);
				try {
					conn.setAutoCommit(autoCommit);
				} catch (SQLException e) {
					throw new IOException(e);
				}

				if (handler != null) {
					handler.handle(conn);
				}

				return new DefaultSqlSession(conn, this.cache, this.configMap);
			}
		}
		return null;
	}

	/*
	 * 获取文件全路径, 如果仅包含文件名则在本地执行目录下寻找同名文件
	 * 
	 * @param filename 文件名(路径)
	 * 
	 * @return 文件全路径, 如果无法找到文件则为null
	 */
	private String getFilePath(String filename) {
		if (filename != null) {
			File file = new File(filename);
			if (file.exists() && file.isFile()) {
				return filename;
			} else {
				filename = ClassLoaderUtils.getResourcePath("") + filename;
				file = new File(filename);
				if (file.exists() && file.isFile()) {
					return filename;
				}
			}
		}
		return null;
	}

}
