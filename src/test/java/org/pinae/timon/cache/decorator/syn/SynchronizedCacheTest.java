package org.pinae.timon.cache.decorator.syn;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.pinae.timon.cache.Cache;
import org.pinae.timon.cache.CacheException;
import org.pinae.timon.cache.CacheFactory;
import org.pinae.timon.cache.KVCacheTestCase;
import org.pinae.timon.cache.decorator.syn.algorithm.FIFOAlgorithm;

/**
 * 同步缓存测试
 * 
 * @author Huiyugeng
 *
 */
public class SynchronizedCacheTest extends KVCacheTestCase {
	protected final Logger log = Logger.getLogger("SynchronizedCacheTest");

	private CacheFactory cacheFactory = CacheFactory.getInstance();

	@Test
	public void testCache() throws CacheException {

		SynchronizedCacheConfiguration config = new SynchronizedCacheConfiguration();
		config.setAlgorithm(new FIFOAlgorithm());
		config.setExpire(5);
		config.setMaxSize(10);
		
		Cache cache = cacheFactory.createCache("testCache", config);
		super.testBasicCache(cache);
		
		cache.close();
	}

}
