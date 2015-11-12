package org.pinae.timon.cache.decorator.ehcache;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.pinae.timon.cache.Cache;
import org.pinae.timon.cache.CacheException;
import org.pinae.timon.cache.CacheFactory;
import org.pinae.timon.cache.KVCacheTestCase;
import org.pinae.timon.cache.decorators.ehcache.EhCacheConfiguration;

/**
 * 基于ehcache缓存测试
 * 
 * @author Huiyugeng
 *
 */
public class EhCacheTest extends KVCacheTestCase {
	protected final Logger logger = Logger.getLogger(EhCacheTest.class);

	private CacheFactory cacheFactory = CacheFactory.getInstance();

	private Cache cache;
	
	@Before
	public void before() throws CacheException {
		EhCacheConfiguration config = new EhCacheConfiguration();
		config.setExpire(5);
		config.setMaxHeapSize(10);
		
		this.cache = cacheFactory.createCache("testCache", config);
	}

	@Test
	public void testCache() throws CacheException {
		super.testBasicCache(cache, true);
		cache.close();
	}
	
	@Test
	public void testOverFlow() throws CacheException {
		super.testOverFlow(cache);
		cache.close();
	}

}
