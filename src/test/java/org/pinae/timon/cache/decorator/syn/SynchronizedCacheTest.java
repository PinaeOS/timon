package org.pinae.timon.cache.decorator.syn;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.pinae.timon.cache.Cache;
import org.pinae.timon.cache.CacheException;
import org.pinae.timon.cache.CacheFactory;
import org.pinae.timon.cache.KVCacheTestCase;
import org.pinae.timon.cache.decorator.syn.eviction.FIFOEvictionPolicy;

/**
 * 同步缓存测试
 * 
 * @author Huiyugeng
 *
 */
public class SynchronizedCacheTest extends KVCacheTestCase {
	protected final Logger log = Logger.getLogger(SynchronizedCacheTest.class);

	private CacheFactory cacheFactory = CacheFactory.getInstance();
	
	private Cache cache;
	
	@Before
	public void before() throws CacheException {
		SynchronizedCacheConfiguration config = new SynchronizedCacheConfiguration();
		config.setEvictionPolicy(new FIFOEvictionPolicy());
		config.setExpire(5);
		config.setMaxHeapSize(10);
		
		this.cache = cacheFactory.createCache("testCache", config);
	}
	
	@Test
	public void testCache() throws CacheException {
		super.testBasicCache(cache);
		cache.close();
	}
	
	@Test
	public void testOverFlow() throws CacheException {
		super.testOverFlow(cache);
		cache.close();
	}

}
