package org.pinae.timon.cache;

import java.io.File;
import java.io.IOException;

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
	
	private static int BYTE = 1;
	private static int KB = 1024 * BYTE;

	private int expire = 3600; // 缓存对象超时时间(s), 0: 永不过期

	private int maxHeapSize = 0; // 缓存长度, 0: 无限长度

	private long maxMemorySize = 0; // 缓存最大占用内存(BYTE), 0: 无限制
	
	private long maxObjectSize = 0; // 缓存对象最大长度(BYTE), 0: 无限制

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
		this.maxMemorySize = config.getLong("cache.memory_max_size", 0) * KB;
		this.maxObjectSize = config.getLong("cache.object_max_size", 0) * KB;
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
	 * @param maxMemorySize 缓存最大占用内存 (KB)
	 */
	public void setMaxMemorySize(long maxMemorySize) {
		if (maxMemorySize < 0) {
			maxMemorySize = 0;
		}
		this.maxMemorySize = maxMemorySize * KB;
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
	 * @param objectSize 缓存对象最大长度(KB)
	 */
	public void setMaxObjectSize(long objectSize) {
		if (objectSize < 0) {
			objectSize = 0;
		}
		this.maxObjectSize = objectSize * KB;
	}
	
	/**
	 * 载入缓存配置 (cache.properties)
	 * 
	 * @return 缓存配置
	 * 
	 * @throws IOException 配置文件读取异常
	 */
	public static CacheConfiguration build() throws IOException {
		File file = new File(ClassLoaderUtils.getResourcePath("") + "cache.properties");
		return build(file);
	}

	/**
	 * 载入缓存配置
	 * 
	 * @param file 配置文件
	 * 
	 * @return 缓存配置
	 * 
	 * @throws IOException 配置文件读取异常
	 */
	public static CacheConfiguration build(File file) throws IOException {
		ConfigMap<String, String> config = ConfigMap.load(file);
		return CacheConfiguration.build(config);
	}

	/**
	 * 根据K-V配置信息生成缓存配置
	 * 
	 * @param cacheConfigMap 缓存的K-V配置信息
	 *  
	 * @return 缓存配置
	 */
	public static CacheConfiguration build(ConfigMap<String, String> cacheConfigMap) {

		if (cacheConfigMap == null || cacheConfigMap.size() == 0) {
			return new SynchronizedCacheConfiguration();
		}
		if ("ENABLE".equals(cacheConfigMap.getString("cache", "ENABLE").toUpperCase())) {
			
			String cacheAdapter = cacheConfigMap.getString("cache.adapter", "SYN").toUpperCase();
			
			if (cacheAdapter.equals("SYN")) {
				return new SynchronizedCacheConfiguration(cacheConfigMap);
			} else if (cacheAdapter.equals("EHCACHE")) {
				return new EhCacheConfiguration(cacheConfigMap);
			}
		}
		return null;
	}

}
