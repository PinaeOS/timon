package org.pinae.timon.cache;

import java.io.IOException;

import org.pinae.timon.cache.decorator.memcached.MemcachedCacheConfiguration;
import org.pinae.timon.cache.decorator.redis.RedisCacheConfiguration;
import org.pinae.timon.cache.decorator.syn.SynchronizedCacheConfiguration;
import org.pinae.timon.cache.decorators.ehcache.EhCacheConfiguration;
import org.pinae.timon.util.ClassLoaderUtils;
import org.pinae.timon.util.ConfigMap;

/**
 * 缓存配置
 * 
 * @author Huiyugeng
 *
 */
public abstract class CacheConfiguration {

	private int expire = 3600; // 缓存对象超时时间(s), 0: 永不过期

	private int maxHeapSize = 0; // 缓存长度, 0: 无限长度

	private long maxMemorySize = 0; // 缓存最大占用内存, 0: 无限制
	
	private long maxObjectSize = 0; // 缓存对象最大长度, 0: 无限制

	/**
	 * 构造函数
	 *
	 */
	public CacheConfiguration() {

	}
	
	/**
	 * 构造函数
	 * 
	 * @param config 配置条目
	 * 
	 */
	public CacheConfiguration(ConfigMap<String, String> config) {
		this.expire = config.getInteger("cache.expire", 600);
		this.maxHeapSize = config.getInteger("cache.heap_max_size", 0);
		this.maxMemorySize = config.getLong("cache.memory_max_size", 0);
		this.maxObjectSize = config.getLong("cache.object_max_size", 0);
	}

	/**
	 * 返回缓存中最多可以存储缓存对象数量
	 * 
	 * @return 缓存对象最大数量
	 */
	public int getMaxHeapSize() {
		return maxHeapSize;
	}

	/**
	 * 设置缓存最大存储容量, 如果设置为0, 则表示没有限制, 默认值为0
	 * 
	 * @param maxHeapSize 缓存最大存储容量
	 */
	public void setMaxHeapSize(int maxHeapSize) {
		this.maxHeapSize = maxHeapSize;
	}

	/**
	 * 返回缓存对象声明周期
	 * 
	 * @return 缓存对象声明周期
	 */
	public int getExpire() {
		return expire;
	}

	/**
	 * 设置缓存对象声明周期, 如果设置为0, 则表示缓存不过期, 且不启动缓存清理器
	 * 
	 * @param expire 缓存对象声明周期
	 */
	public void setExpire(int expire) {
		this.expire = expire;
	}

	/**
	 * 返回缓存最大占用内存
	 * 
	 * @return 缓存最大占用内存
	 */
	public long getMaxMemorySize() {
		return maxMemorySize;
	}

	/**
	 * 设置缓存最大占用内存, 如果设置为0, 则表示没有限制, 默认值为0
	 * 
	 * @param maxMemorySize 缓存最大占用内存
	 */
	public void setMaxMemorySize(long maxMemorySize) {
		this.maxMemorySize = maxMemorySize;
	}

	/**
	 * 返回缓存对象最大长度
	 * 
	 * @return 缓存对象最大长度
	 */
	public long getMaxObjectSize() {
		return maxObjectSize;
	}

	/**
	 * 设置缓存对象最大长度, 如果设置为0, 则表示没有此限制, 默认值为0
	 * 
	 * @param objectSize 缓存对象最大长度
	 */
	public void setMaxObjectSize(long objectSize) {
		this.maxObjectSize = objectSize;
	}
	
	/**
	 * 载入缓存配置 (cache.properties)
	 * 
	 * @return 缓存配置
	 * 
	 * @throws IOException 配置文件读取异常
	 */
	public static CacheConfiguration loadConfig() throws IOException {
		return loadConfig(ClassLoaderUtils.getResourcePath("") + "cache.properties");
	}

	/**
	 * 载入缓存配置
	 * 
	 * @param filename 配置文件
	 * 
	 * @return 缓存配置
	 * 
	 * @throws IOException 配置文件读取异常
	 */
	public static CacheConfiguration loadConfig(String filename) throws IOException {
		ConfigMap<String, String> config = ConfigMap.loadFromFile(filename);
		return CacheConfiguration.getConfig(config);
	}

	/**
	 * 根据K-V配置信息生成缓存配置
	 * 
	 * @param config K-V配置信息
	 *  
	 * @return 缓存配置
	 */
	public static CacheConfiguration getConfig(ConfigMap<String, String> config) {

		if (config == null || config.size() == 0) {
			return new SynchronizedCacheConfiguration();
		}
		if (config.getBoolean("cache", true)) {
			
			String cacheAdapter = config.getString("cache.adapter", "syn").toLowerCase();
			
			if (cacheAdapter.equalsIgnoreCase("syn")) {
				return new SynchronizedCacheConfiguration(config);
			} else if (cacheAdapter.equalsIgnoreCase("ehcache")) {
				return new EhCacheConfiguration(config);
			} else if (cacheAdapter.equalsIgnoreCase("memcached")) {
				return new MemcachedCacheConfiguration(config);
			} else if (cacheAdapter.equalsIgnoreCase("redis")) {
				return new RedisCacheConfiguration(config);
			}
		}
		return null;
	}

}
