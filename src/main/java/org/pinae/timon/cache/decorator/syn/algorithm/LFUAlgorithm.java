package org.pinae.timon.cache.decorator.syn.algorithm;

import org.pinae.timon.cache.CacheObject;

/**
 * 最少访问缓存清理策略
 * 
 * @author Huiyugeng
 *
 */
public class LFUAlgorithm implements Algorithm {

	public int compare(CacheObject o1, CacheObject o2) {
		CacheObject co1 = (CacheObject) o1;
		CacheObject co2 = (CacheObject) o2;

		if (co1.getHitCount() < co2.getHitCount()) {
			return -1;
		} else {
			if (co1.getHitCount() == co2.getHitCount()) {
				if (co1.getId() < co2.getId()) {
					return -1;
				} else {
					if (co1.getId() == co2.getId()) {
						return 0;
					} else {
						return 1;
					}
				}
			} else {
				return 1;
			}
		}
	}

}
