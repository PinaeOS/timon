package org.pinae.timon.cache.decorator.memcached;

import org.apache.log4j.Logger;
import org.junit.Before;
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
	protected final Logger log = Logger.getLogger(MemcachedCacheTest.class);

	private CacheFactory cacheFactory = CacheFactory.getInstance();

	private Cache cache;
	
	private String memcachedServer = "localhost:11211";
	
	@Before
	public void before() throws CacheException {
		MemcachedCacheConfiguration config = new MemcachedCacheConfiguration();
		
		config.setServer(memcachedServer);
		
		config.setExpire(0);
		config.setMaxHeapSize(10);
		
		this.cache = cacheFactory.createCache("testCache", config);
	}

	@Test
	public void testCache() throws CacheException {
		super.testBasicCache(cache, false);
		cache.close();
	}

}
