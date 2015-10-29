package org.pinae.timon.cache.decorator.syn.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.pinae.timon.cache.CacheObject;
import org.pinae.timon.cache.decorator.syn.eviction.LRUEvictionPolicy;
import org.pinae.timon.util.MessageDigestUtils;

public class LRUAlgorithmTest {
	
	private static Logger logger = Logger.getLogger(LRUAlgorithmTest.class);
	
	public List<CacheObject> getTestData(int size) {
		
		List<CacheObject> objList = new ArrayList<CacheObject>();
		for (int i = 0; i < size; i++) {
			CacheObject cacheObj = new CacheObject(Long.toString(i), MessageDigestUtils.MD5(Long.toString(i)), size - i);
			objList.add(cacheObj);
		}
		return objList;
	}
	
	@Test
	public void testCompare() {
		List<CacheObject> objList = getTestData(500);
		
		long start = System.currentTimeMillis();
		
		Collections.sort(objList, new LRUEvictionPolicy());
		
		logger.info("LRU sort Used:" + Long.toString(System.currentTimeMillis() - start) + " ms");
	}
}
