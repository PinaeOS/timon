package org.pinae.timon.cache;

import org.pinae.timon.util.ConfigMap;

/**
 * 缓存配置
 * 
 * @author Huiyugeng
 *
 */
public class CacheConfiguration {

	private int expire = 3600; // 缓存对象超时时间(s), 0: 永不过期

	private int maxSize = 0; // 缓存长度, 0: 无限长度

	private long maxMemorySize = 0; // 缓存占用内存, 0: 无限制

	/**
	 * 构造函数
	 *
	 */
	public CacheConfiguration() {
		super();
	}

	/**
	 * 返回缓存中最多可以存储缓存对象数量
	 * 
	 * @return 缓存对象最大数量
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * 设置缓存最大存储容量,如果设置为0,则表示没有限制,默认值为0
	 * 
	 * @param maxSize 缓存最大存储容量
	 */
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
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
	 * 设置缓存对象声明周期 如果设置为0,则表示缓存不过期,且不启动缓存清理器
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
	 * 设置缓存最大占用内存,如果设置为0,则表示没有限制, 默认值为0
	 * 
	 * @param maxMemorySize 缓存最大占用内存
	 */
	public void setMaxMemorySize(long maxMemorySize) {
		this.maxMemorySize = maxMemorySize;
	}


	public static CacheConfiguration getConfig(ConfigMap<String, String> config) {
		return null;
	}

}
