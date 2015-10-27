package org.pinae.timon.cache.decorator.syn;

import org.pinae.timon.cache.CacheConfiguration;
import org.pinae.timon.cache.decorator.syn.algorithm.Algorithm;
import org.pinae.timon.cache.decorator.syn.algorithm.LFUAlgorithm;

/**
 * 同步缓存配置
 * 
 * @author Huiyugeng
 *
 */
public final class SynchronizedCacheConfiguration extends CacheConfiguration {

	private Algorithm algorithm = new LFUAlgorithm(); // 清理算法
	
	private int sortLength = 10000; // 排序长度, 0: 全部进行排序

	/**
	 * 构造函数
	 */
	public SynchronizedCacheConfiguration() {
		super();
	}
	
	/**
	 * 返回缓存清理策略（FIFO/LFU/LRU）
	 * 
	 * @return 缓存清理策略
	 */
	public Algorithm getAlgorithm() {
		return algorithm;
	}

	/**
	 * 设置缓存清理策略（FIFO/LFU/LRU）
	 * 
	 * @param algorithm 缓存清理策略
	 */
	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * 返回缓存排序长度
	 * 
	 * @return 排序长度
	 */
	public int getSortLength() {
		return sortLength;
	}

	/**
	 * 设置排序长度, 仅对缓存中指定长度的元素进行排序
	 * 
	 * @param sortLength 排序长度
	 */
	public void setSortLength(int sortLength) {
		this.sortLength = sortLength;
	}

}