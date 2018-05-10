package org.pinae.timon.cache.decorator.syn;

import org.pinae.timon.cache.CacheConfiguration;
import org.pinae.timon.cache.decorator.syn.eviction.EvictionPolicy;
import org.pinae.timon.cache.decorator.syn.eviction.FIFOEvictionPolicy;
import org.pinae.timon.cache.decorator.syn.eviction.LFUEvictionPolicy;
import org.pinae.timon.cache.decorator.syn.eviction.LRUEvictionPolicy;
import org.pinae.timon.util.ConfigMap;

/**
 * 同步缓存配置
 * 
 * @author Huiyugeng
 *
 */
public final class SynchronizedCacheConfiguration extends CacheConfiguration {

	private EvictionPolicy evictionPolicy = new LFUEvictionPolicy(); // 对象回收策略
	
	private int sortLength = 10000; // 排序长度, 0: 全部进行排序

	/**
	 * 构造函数
	 */
	public SynchronizedCacheConfiguration() {
		super();
	}
	
	/**
	 * 构造函数
	 * 
	 * @param config 配置条目
	 * 
	 */
	public SynchronizedCacheConfiguration(ConfigMap<String, String> config) {
		super(config);
		String algorithm = config.getString("cache.syn.eviction", "lfu").toLowerCase();
		
		if (algorithm.equals("lru")) {
			this.evictionPolicy = new LRUEvictionPolicy();
		} else if (algorithm.equals("fifo")) {
			this.evictionPolicy = new FIFOEvictionPolicy();
		} else {
			this.evictionPolicy = new LFUEvictionPolicy();
		}
		this.sortLength = config.getInteger("cache.syn.sort_length", 100000);
	}
	
	/**
	 * 返回缓存清理策略（FIFO/LFU/LRU）
	 * 
	 * @return 缓存清理策略
	 */
	public EvictionPolicy getEvictionPolicy() {
		return evictionPolicy;
	}

	/**
	 * 设置缓存清理策略（FIFO/LFU/LRU）
	 * 
	 * @param algorithm 缓存清理策略
	 */
	public void setEvictionPolicy(EvictionPolicy algorithm) {
		this.evictionPolicy = algorithm;
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
