package org.pinae.timon.cache.decorators.ehcache;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.pinae.timon.cache.AbstractCache;
import org.pinae.timon.cache.CacheException;
import org.pinae.timon.util.ObjectUtils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.MemoryUnit;

/**
 * 基于ehcache的缓存
 * 
 * @author Huiyugeng
 *
 */
public class EhCache extends AbstractCache {
	
	private static Logger logger = Logger.getLogger(EhCache.class);
	
	private static CacheManager Manager;

	private EhCacheConfiguration config; // 缓存配置

	private Cache cache;

	public EhCache(String name, EhCacheConfiguration config) {
		super(name);

		String configUrl = config.getConfigUrl();
		if (configUrl == null) {
			
			CacheConfiguration cacheConfig = new CacheConfiguration();
			cacheConfig.setName(name);
			cacheConfig.setMemoryStoreEvictionPolicy(config.getEvictionPolicy());
			cacheConfig.setMaxEntriesLocalHeap(config.getMaxHeapSize());
			
			if (config.getMaxMemorySize() > 0) {
				cacheConfig.setMaxBytesLocalHeap(MemoryUnit.BYTES.toBytes(config.getMaxMemorySize()));
			}
			if (config.getExpire() > 0) {
				cacheConfig.timeToLiveSeconds(config.getExpire());
			}
			if (EhCache.Manager == null) {
				EhCache.Manager = new CacheManager();
			}
			this.cache = new Cache(cacheConfig);
			
			EhCache.Manager.addCache(this.cache);
		} else {
			EhCache.Manager = CacheManager.newInstance(configUrl);
			this.cache = EhCache.Manager.getCache(name);
		}
		
		this.config = config;
	}

	public org.pinae.timon.cache.CacheConfiguration getCacheConfig() {
		return config;
	}

	public void put(String key, Object value) throws CacheException {
		put(key, value, this.config.getExpire());
	}
	
	@SuppressWarnings("deprecation")
	public void put(String key, Object value, int expire) throws CacheException {
		
		if (key == null) {
			throw new NullPointerException("cache key can't be NULL");
		}
		if (value == null) {
			throw new NullPointerException("cache value can't be NULL");
		}
		
		if (key != null && value != null) {
			
			Element element = new Element(key, value);
			if (expire > 0) {
				element.setTimeToLive(expire);
			}
			long objectSize = ObjectUtils.size(element);
			
			if (config.getMaxObjectSize() == 0 || objectSize <= config.getMaxObjectSize()) {
				cache.put(element);
				
				info.incPuts(true);
				info.setSize(cache.getSize()); // 重新修正缓存对象数量
				info.setMemorySize(cache.getMemoryStoreSize());
			} else {
				logger.info(String.format("Object size over max_object_size : key=%s, object_size=%d, max_object_size=%d", 
						StringUtils.abbreviate(key, 15), objectSize, config.getMaxObjectSize()));
			}
		}
		
	}

	public Object get(String key) throws CacheException {
		Object value = null;
		if (key != null) {
			value = cache.get(key);
		}
		if (value != null) {
			info.incHits();
		} else {
			info.incMisses();
		}
		
		if (value != null && value instanceof Element) {
			value = ((Element)value).getObjectValue();
		}
		return value;
	}

	@SuppressWarnings("deprecation")
	public boolean remove(String key) throws CacheException {
		if (key != null) {
			
			if (cache.remove(key)) {
				
				info.incRemoves();
				info.setSize(cache.getSize());
				info.setMemorySize(cache.getMemoryStoreSize());
				
				return true;
			}
		}
		return false;
	}

	public void clear() throws CacheException {
		cache.removeAll();
		info.setSizeToZero();
	}

	public void close() throws CacheException {
		Manager.removeCache(cache.getName());
	}



}
