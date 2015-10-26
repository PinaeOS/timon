package org.pinae.timon.cache;

import static junit.framework.TestCase.assertEquals;

import org.pinae.timon.util.MessageDigestUtils;

/**
 * Key-Value类型缓存测试
 * 
 * @author Huiyugeng
 *
 */
public class KVCacheTestCase {

	public void testBasicCache(Cache cache) throws CacheException {

		// 清理缓存中的数据
		cache.clear();

		// 测试Put
		cache.put("name", "huiyugeng");
		cache.put("age", "27");
		cache.put("sex", "male");
		assertEquals(cache.getCacheInfo().getTotalPuts(), 3);

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

		// 测试RemoveAll
		cache.clear();
		assertEquals(cache.get("sex"), null);
		assertEquals(cache.getCacheInfo().getSize(), 0);

	}
	
	public void testOverFlow(Cache cache) throws CacheException {
		long cacheSize = cache.getCacheConfig().getMaxSize();
		
		for (int i = 0 ; i < cacheSize * 2; i++) {
			cache.put(Integer.toString(i), MessageDigestUtils.MD5(Integer.toString(i)));
		}
	}
}
