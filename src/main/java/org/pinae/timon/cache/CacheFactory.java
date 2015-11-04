package org.pinae.timon.cache;

import java.util.HashMap;
import java.util.Map;

import org.pinae.timon.cache.decorator.memcached.MemcachedCache;
import org.pinae.timon.cache.decorator.memcached.MemcachedCacheConfiguration;
import org.pinae.timon.cache.decorator.syn.SynchronizedCache;
import org.pinae.timon.cache.decorator.syn.SynchronizedCacheConfiguration;
import org.pinae.timon.cache.decorators.ehcache.EhCache;
import org.pinae.timon.cache.decorators.ehcache.EhCacheConfiguration;

/**
 * 缓存生成工厂类
 * 
 * @author Huiyugeng
 *
 */
public class CacheFactory {

	private static final CacheFactory factory = new CacheFactory();

	/**
	 * 缓存池(缓存名称，缓存)
	 */
	private Map<String, Cache> cachePool;

	private CacheFactory() {
		this.cachePool = new HashMap<String, Cache>();
	}

	/**
	 * 获得缓存生成工厂实例
	 * 
	 * @return 缓存工厂实例
	 */
	public synchronized static CacheFactory getInstance() {
		return factory;
	}

	/**
	 * 生成缓存
	 * 
	 * @param name 缓存名称
	 * @param config 缓存配置
	 * @param type 缓存类别
	 * 
	 * @return 生成的缓存对象
	 * @throws CacheException 异常处理
	 */
	public Cache createCache(String name, CacheConfiguration config, int type) throws CacheException {
		if (config == null) {
			throw new CacheException("Cache configuration is null");
		}
		
		Cache cache = null;

		switch (type) {
		case Cache.SYN_CACHE:
			cache = new SynchronizedCache(name, (SynchronizedCacheConfiguration) config);
			break;
		case Cache.EHCACHE_CACHE:
			cache = new EhCache(name, (EhCacheConfiguration) config);
			break;
		case Cache.MEMCACHED_CACHE:
			cache = new MemcachedCache(name, (MemcachedCacheConfiguration)config);
			break;
		}

		if (name != null && cache != null) {
			this.cachePool.put(name, cache);
		}
		return cache;
	}

	/**
	 * 生成缓存，默认生成SynchronizedCache
	 * 
	 * @param name 缓存名称
	 * 
	 * @return 生成的缓存对象
	 * @throws CacheException 异常处理
	 */
	public Cache createCache(String name) throws CacheException {
		SynchronizedCacheConfiguration config = new SynchronizedCacheConfiguration();
		return createCache(name, config, Cache.SYN_CACHE);
	}

	/**
	 * 生成名为defalt的缓存
	 * 
	 * @param config 缓存配置
	 * @return 生成的缓存对象
	 * @throws CacheException 异常处理
	 */
	public Cache createCache(CacheConfiguration config) throws CacheException {
		return createCache("default", config);
	}

	/**
	 * 生成缓存
	 * 
	 * @param name 缓存名称
	 * @param config 缓存配置
	 * 
	 * @return 生成的缓存对象
	 * @throws CacheException
	 */
	public Cache createCache(String name, CacheConfiguration config) throws CacheException {
		
		// 默认使用同步缓存
		int type = Cache.SYN_CACHE; 
		
		if (config instanceof EhCacheConfiguration) {
			type = Cache.EHCACHE_CACHE;
		} else if (config instanceof MemcachedCacheConfiguration) {
			type = Cache.MEMCACHED_CACHE;
		}
		return createCache(name, config, type);
	}

	/**
	 * 从缓存池中清理指定名称的缓存
	 * 
	 * @param name 缓存名称
	 * 
	 * @return 被清理的缓存
	 */
	public Cache removeCache(String name) {
		return this.cachePool.remove(name);
	}

	/**
	 * 从缓存池中获取指定名称的缓存
	 * 
	 * @param name 缓存名称
	 * @return 指定的缓存
	 */
	public Cache getCache(String name) {
		return this.cachePool.get(name);
	}

}
