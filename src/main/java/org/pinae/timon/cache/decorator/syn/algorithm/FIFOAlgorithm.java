package org.pinae.timon.cache.decorator.syn.algorithm;

import org.pinae.timon.cache.CacheObject;

/**
 * 先入先出缓存清理策略
 * 
 * @author Huiyugeng
 *
 */
public class FIFOAlgorithm implements Algorithm {

	public int compare(CacheObject o1, CacheObject o2) {

		if (o1.getCreateTime().getTime() < o2.getCreateTime().getTime()) {
			return -1;
		} else {
			if (o1.getCreateTime().getTime() == o2.getCreateTime().getTime()) {
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
