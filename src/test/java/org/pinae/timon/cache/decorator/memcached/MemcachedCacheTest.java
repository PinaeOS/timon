package org.pinae.timon.cache.decorator.memcached;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.pinae.timon.cache.Cache;
import org.pinae.timon.cache.CacheException;
import org.pinae.timon.cache.CacheFactory;
import org.pinae.timon.cache.KVCacheTestCase;

/**
 * 基于memcached缓存测试
 * 
 * @author Huiyugeng
 *
 */
public class MemcachedCacheTest extends KVCacheTestCase {
	protected final Logger logger = Logger.getLogger(MemcachedCacheTest.class);

	private CacheFactory cacheFactory = CacheFactory.getInstance();

	private Cache cache;
	
	private String memcachedServer = "192.168.228.132:11211";
	
	public void createCache(int size) throws CacheException {
		MemcachedCacheConfiguration config = new MemcachedCacheConfiguration();
		
		config.setServer(memcachedServer);
		
		config.setExpire(0);
		config.setMaxHeapSize(size);
		
		this.cache = cacheFactory.createCache("testCache", config);
		this.cache.clear();
	}

	@Test
	public void testCache() throws CacheException {
		createCache(10);
		super.testBasicCache(cache, false);
		cache.close();
	}

	@Test
	public void testPut() throws CacheException {
		createCache(10000);
		super.testPut(cache, 10000, 1024);
		cache.close();
	}
}
