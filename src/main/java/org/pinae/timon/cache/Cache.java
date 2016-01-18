package org.pinae.timon.cache;

import org.pinae.timon.cache.CacheException;

/**
 * 缓存接口
 * 
 * 缓存采用键值进行存储, 通过缓存键查找缓存值
 * 
 * @author Huiyugeng
 *
 */
public interface Cache {
	/**
	 * 同步缓存
	 */
	public static final int SYN_CACHE = 0;
	
	/**
	 * Ehcache缓存
	 */
	public static final int EHCACHE_CACHE = 1;
	
	/**
	 * Memcached缓存
	 */
	public static final int MEMCACHED_CACHE = 2;
	/**
	 * 获取缓存信息
	 * 
	 * @return 缓存对象信息
	 */
	public CacheInformation getCacheInfo();

	/**
	 * 获取缓存配置信息
	 * 
	 * @return 缓存配置信息
	 */
	public CacheConfiguration getCacheConfig();

	/**
	 * 向缓存中添加对象
	 * 
	 * @param key 缓存键
	 * @param value 缓存值
	 * @throws CacheException 缓存异常
	 */
	public void put(String key, Object value) throws CacheException;
	
	/**
	 * 将需要缓存的键和值加入到缓存中, 并设置缓存对象超时时间
	 * 
	 * @param key 缓存名称
	 * @param value 缓存的值
	 * @param expire 缓存过期时间 (s)
	 * @throws CacheException 缓存异常
	 */
	public void put(String key, Object value, int expire) throws CacheException;
	
	/**
	 * 根据指定名称从缓存中获取对象
	 * 
	 * @param key 指定名称
	 * @return 缓存的对象
	 * @throws CacheException 缓存异常
	 */
	public Object get(String key) throws CacheException;
	
	/**
	 * 将指定名称的缓存对象对缓存中移除
	 * 
	 * @param key 指定的缓存名称
	 * @return 是否移除成功
	 * @throws CacheException 缓存异常
	 */
	public boolean remove(String key) throws CacheException;

	/**
	 * 将所有缓存对象移除缓存
	 *
	 * @throws CacheException 缓存异常
	 */
	public void clear() throws CacheException;

	/**
	 * 关闭缓存
	 * 
	 * @throws CacheException 缓存异常
	 */
	public void close() throws CacheException;
}
