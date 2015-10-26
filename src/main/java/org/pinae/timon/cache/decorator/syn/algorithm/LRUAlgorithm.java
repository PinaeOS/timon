package org.pinae.timon.cache.decorator.syn.algorithm;

import org.pinae.timon.cache.CacheObject;

/**
 * 最后使用缓存清理策略
 * 
 * @author Huiyugeng
 *
 */
public class LRUAlgorithm implements Algorithm {

	public int compare(CacheObject o1, CacheObject o2) {

		if (o1.getLastAccessTime().getTime() < o2.getLastAccessTime().getTime()) {
			return -1;
		} else {
			if (o1.getLastAccessTime().getTime() == o2.getLastAccessTime().getTime()) {
				if (o1.getId() < o2.getId()) {
					return -1;
				} else {
					if (o1.getId() == o2.getId()) {
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
