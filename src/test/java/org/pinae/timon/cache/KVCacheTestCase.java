package org.pinae.timon.cache;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import org.junit.Test;
import org.pinae.timon.util.MessageDigestUtils;

/**
 * Key-Value类型缓存测试
 * 
 * @author Huiyugeng
 *
 */
public class KVCacheTestCase {

	public void testBasicCache(Cache cache) throws CacheException {

		// 测试Put
		cache.put("name", "huiyugeng");
		assertEquals(cache.getCacheInfo().getTotalPuts(), 1);
		long memorySize = cache.getCacheInfo().getMemorySize();
		
		cache.put("age", "27");
		assertEquals(cache.getCacheInfo().getTotalPuts(), 2);
		assertTrue(cache.getCacheInfo().getMemorySize() > memorySize); // 测试内存增长
		memorySize = cache.getCacheInfo().getMemorySize();
		
		cache.put("sex", "male");
		assertEquals(cache.getCacheInfo().getTotalPuts(), 3);
		assertTrue(cache.getCacheInfo().getMemorySize() > memorySize); // 测试内存增长
		memorySize = cache.getCacheInfo().getMemorySize();
		
		// 测试Get Hit
		assertEquals(cache.get("name"), "huiyugeng");
		assertEquals(cache.get("age"), "27");
		assertEquals(cache.get("sex"), "male");
		assertEquals(cache.getCacheInfo().getHits(), 3);

		// 测试Get Miss
		assertEquals(cache.get("phone"), null);
		assertEquals(cache.get("address"), null);
		assertEquals(cache.getCacheInfo().getMisses(), 2);

		// 测试Remove
		assertEquals(cache.remove("name"), true);
		assertEquals(cache.getCacheInfo().getTotalRemoves(), 1);
		assertEquals(cache.getCacheInfo().getSize(), 2);
		assertTrue(cache.getCacheInfo().getMemorySize() < memorySize); // 测试内存减少
		
		// 测试Clear
		cache.clear();
		assertEquals(cache.get("sex"), null);
		assertEquals(cache.getCacheInfo().getSize(), 0);
		assertEquals(cache.getCacheInfo().getMemorySize(), 0);
	}
	
	public void testOverFlow(Cache cache) throws CacheException {
		long cacheSize = cache.getCacheConfig().getMaxHeapSize();
		
		for (int i = 0 ; i < cacheSize * 2; i++) {
			cache.put(Integer.toString(i), MessageDigestUtils.MD5(Integer.toString(i)));
		}
		
		assertEquals(cache.getCacheInfo().getSize(), cacheSize);
	}
	
}
