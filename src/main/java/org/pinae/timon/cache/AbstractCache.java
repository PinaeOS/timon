package org.pinae.timon.cache;

/**
 * 缓存抽象类
 * 
 * @author Huiyugeng
 *
 */
public abstract class AbstractCache implements Cache {
	
	/* 缓存信息 */
	protected CacheInformation info;
	
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
