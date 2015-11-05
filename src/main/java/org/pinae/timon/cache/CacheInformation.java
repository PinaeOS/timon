package org.pinae.timon.cache;

/**
 * 缓存信息
 * 
 * @author Huiyugeng
 *
 */
public class CacheInformation {

	private String name; // 缓存名称

	private long hits = 0; // 缓存命中次数

	private long misses = 0; // 缓存未命中次数

	private long createTime = System.currentTimeMillis(); // 缓存建立时间

	private long totlePuts = 0; // 缓存加入计数器

	private long totleRemoves = 0; // 缓存移除计数器

	private long size = 0; // 缓存长度

	private long memorySize = 0; // 缓存占用内存长度

	public CacheInformation(String name) {
		this.name = name;
	}

	public void incHits() {
		this.hits++;
	}

	public void incMisses() {
		this.misses++;
	}

	public void incPuts(boolean incSize) {
		if (incSize) {
			this.size++;
		}
		this.totlePuts++;
	}

	public void incRemoves() {
		this.size--;
		this.totleRemoves++;
	}

	public void addMemorySize(long size) {
		this.memorySize = this.memorySize + size;
	}

	public void minusMemorySize(long size) {
		this.memorySize = this.memorySize - size;
	}

	public void setSizeToZero() {
		this.size = 0;
		this.memorySize = 0;
	}

	/**
	 * 返回缓存命中次数
	 * 
	 * @return 缓存命中次数
	 */
	public long getHits() {
		return hits;
	}

	/**
	 * 返回缓存没有命中次数
	 * 
	 * @return 缓存没有命中次数
	 */
	public long getMisses() {
		return misses;
	}

	/**
	 * 返回缓存建立时间
	 * 
	 * @return 缓存建立时间
	 */
	public long getCreateTime() {
		return createTime;
	}

	/**
	 * 返回缓存占用内存长度
	 * 
	 * @return 缓存占用内存长度
	 */
	public long getMemorySize() {
		return memorySize;
	}

	/**
	 * 返回缓存中缓存对象数量
	 * 
	 * @return 缓存中缓存对象数量
	 */
	public long getSize() {
		return size;
	}

	/**
	 * 返回缓存总共添加缓存对象次数
	 * 
	 * @return 缓存总共添加缓存对象次数
	 */
	public long getTotalPuts() {
		return totlePuts;
	}

	/**
	 * 返回缓存总共移除缓存对象次数
	 * 
	 * @return 缓存总共移除缓存对象次数
	 */
	public long getTotalRemoves() {
		return totleRemoves;
	}

	/**
	 * 返回缓存名称
	 * 
	 * @return 缓存名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置缓存中缓存对象数量
	 * 
	 * @param size 缓存中缓存对象数量
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * 缓存占用内存长度
	 * 
	 * @param memorySize 缓存占用内存长度
	 */
	public void setMemorySize(long memorySize) {
		this.memorySize = memorySize;
	}

}
