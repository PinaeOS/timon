package org.pinae.timon.cache.decorator.syn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.pinae.timon.cache.AbstractCache;
import org.pinae.timon.cache.CacheConfiguration;
import org.pinae.timon.cache.CacheObject;

/**
 * 同步缓存 每次只能有一个线程对缓存内容进行操作
 * 
 * @author Huiyugeng
 *
 */
public class SynchronizedCache extends AbstractCache {

	private static Logger logger = Logger.getLogger(SynchronizedCache.class);
	
	private SynchronizedCacheConfiguration config; // 缓存配置信息

	private Map<String, CacheObject> cache; // 缓存内容

	/**
	 * 构造函数
	 * 
	 * @param name 缓存名称
	 * @param config 缓存配置
	 */
	public SynchronizedCache(String name, CacheConfiguration config) {
		super(name);
		
		this.config = (SynchronizedCacheConfiguration) config;
		this.cache = new ConcurrentHashMap<String, CacheObject>();
	}

	/*
	 * 更新缓存对象的顺序
	 */
	private List<CacheObject> sort() {
		List<CacheObject> cacheObjectList = new ArrayList<CacheObject>(cache.values());

		if (config.getSortLength() != 0) {
			int end = config.getSortLength();
			if (cacheObjectList.size() < config.getSortLength()) {
				end = cacheObjectList.size();
			}
			cacheObjectList = cacheObjectList.subList(0, end);
		}
		Collections.sort(cacheObjectList, config.getEvictionPolicy());

		return cacheObjectList;
	}

	/*
	 * 检测缓存长度是否越界
	 */
	private boolean checkOverFlow() {
		// 检查长度是否越界
		if (config.getMaxHeapSize() > 0 && info.getSize() >= config.getMaxHeapSize()) {
			return true;
		}

		// 检查占用空间是否越界
		if (config.getMaxMemorySize() > 0 && info.getMemorySize() >= config.getMaxMemorySize()) {
			return true;
		}

		return false;
	}

	/*
	 * 根据预设的缓存清理策略进行缓存清理 
	 */
	private synchronized void clean() {
		
		while (checkOverFlow()) {

			List<CacheObject> cacheObjectList = sort();

			if (cacheObjectList != null && cacheObjectList.size() > 0) {
				CacheObject cacheObject = (CacheObject) cacheObjectList.get(0);
				remove(cacheObject.getKey());
			}
		}
	}
	
	public synchronized void put(String key, Object value) {
		put(key, value, -1);
	}

	public synchronized void put(String key, Object value, int expire) {
		if (key == null) {
			throw new NullPointerException("cache key can't be NULL");
		}
		if (value == null) {
			throw new NullPointerException("cache value can't be NULL");
		}
		
		// 向缓存中增加缓存对象
		CacheObject cacheObject = new CacheObject(key, value, expire);
		long objectSize = cacheObject.getSize();
		
		// 判断对象长度是否超出最大限制
		if (config.getMaxObjectSize() == 0 || objectSize <= config.getMaxObjectSize()) {
			
			// 执行缓存清理
			clean();
			
			CacheObject replaceObject = cache.get(key);
			
			// 缓存对象加入缓存中
			cache.put(key, cacheObject);

			if (replaceObject != null) {
				info.incPuts(false); //如果非替换模式, 仅增长totalPuts
				info.minusMemorySize(replaceObject.getSize());
			} else {
				info.incPuts(true);
			}
			info.addMemorySize(objectSize);
			

		} else {
			logger.info(String.format("Object size over max_object_size : key=%s, object_size=%d, max_object_size=%d", 
					StringUtils.abbreviate(key, 15), objectSize, config.getMaxObjectSize()));
		}
	}

	public synchronized Object get(String key) {
		CacheObject cacheObject = cache.get(key);

		Object value = null;
		
		if (cacheObject != null) {
			// 对象超时判断: 优先判断缓存对象超时, 再判断缓存超时
			long expire = (cacheObject.getExpire() > 0 ? cacheObject.getExpire() : config.getExpire()) * 1000;
			
			long now = System.currentTimeMillis();
			long createTime = cacheObject.getCreateTime().getTime();
			
			if (Math.abs(now - createTime) > expire) {
				cache.remove(key);
			} else {
				if (cacheObject.getValue() != null) {
					value = cacheObject.getValue();
					info.incHits();
				}
			}
		} else {
			info.incMisses();
		}
		
		return value;
	}

	public CacheConfiguration getCacheConfig() {
		return config;
	}


	public synchronized boolean remove(String key) {
		CacheObject cacheObj = cache.remove(key);

		if (cacheObj != null) {
			info.incRemoves();
			info.minusMemorySize(cacheObj.getSize());

			return true;
		} else {
			return false;
		}
	}

	public synchronized void clear() {
		cache.clear();
		info.setSizeToZero();
		
		logger.info("Clear Cache:" + info.getName());
	}

	public void close() {
		cache.clear();
		cache = null;
	}

}
