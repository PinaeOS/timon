package org.pinae.timon.cache;

public abstract class AbstractCache implements Cache {
	
	protected CacheInformation info; // 缓存信息
	
	public AbstractCache(String name) {
		this.info = new CacheInformation(name);
	}

	public CacheInformation getCacheInfo() {
		return info;
	}

	public abstract void put(String key, Object object) throws CacheException;

	public abstract Object get(String key) throws CacheException;

	public abstract boolean remove(String key) throws CacheException;

	public abstract void clear() throws CacheException;

	public abstract void close() throws CacheException;

}
