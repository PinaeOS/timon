package org.pinae.timon.cache.decorator.memcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import org.pinae.timon.cache.AbstractCache;
import org.pinae.timon.cache.CacheConfiguration;
import org.pinae.timon.cache.CacheException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

/**
 * 基于memcached的缓存
 * 
 * @author Huiyugeng
 * 
 */
public class MemcachedCache extends AbstractCache {

	private MemcachedCacheConfiguration config; // 缓存配置

	private MemcachedClient client; // memcached客户端

	public MemcachedCache(String name, CacheConfiguration config) throws CacheException {
		super(name);
		
		this.config = (MemcachedCacheConfiguration) config;

		String server = this.config.getServer();

		int weights[] = this.config.getWeights();

		MemcachedClientBuilder builder = null;

		if (weights == null || weights.length == 0) {
			builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(server));
		} else {
			builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(server), weights);
		}

		if (this.config.getAuth() != null && this.config.getAuth().size() > 0) {
			builder.setAuthInfoMap(this.config.getAuth());
		}

		builder.setName(name);
		builder.setConnectionPoolSize(this.config.getPoolSize());
		builder.setFailureMode(this.config.isFailureMode());

		try {
			client = builder.build();
		} catch (IOException e) {
			throw new CacheException(e);
		}

		// 初始化缓存长度
		Map<String, String> itemCountMap = getStats("curr_items");

		long size = 0;
		for (Entry<String, String> itemCount : itemCountMap.entrySet()) {
			size += Long.parseLong(itemCount.getValue());
		}
		
		info.setSize(size);

	}

	public CacheConfiguration getCacheConfig() {
		return config;
	}

	public void put(String key, Object value) throws CacheException {
		put(key, value, this.config.getExpire());
	}
	
	public void put(String key, Object value, int expire) throws CacheException {
		try {
			if (client.get(key) == null) {
				client.set(key, expire, value);
				info.incPuts(true);
			} else {
				client.replace(key, expire, value);
				info.incPuts(false);
			}
		} catch (TimeoutException e) {
			throw new CacheException(e);
		} catch (InterruptedException e) {
			throw new CacheException(e);
		} catch (MemcachedException e) {
			throw new CacheException(e);
		}

	}

	public Object get(String key) throws CacheException {
		Object value = null;

		try {
			value = client.get(key);

			if (value != null) {
				info.incHits();
			} else {
				info.incMisses();
			}

		} catch (TimeoutException e) {
			throw new CacheException(e);
		} catch (InterruptedException e) {
			throw new CacheException(e);
		} catch (MemcachedException e) {
			throw new CacheException(e);
		}

		return value;
	}

	public boolean remove(String key) throws CacheException {
		boolean result = false;

		try {
			result = client.delete(key);
			info.incRemoves();
		} catch (TimeoutException e) {
			throw new CacheException(e);
		} catch (InterruptedException e) {
			throw new CacheException(e);
		} catch (MemcachedException e) {
			throw new CacheException(e);
		}

		return result;
	}

	public void clear() throws CacheException {
		try {
			client.flushAll();
			info.setSizeToZero();
		} catch (TimeoutException e) {
			throw new CacheException(e);
		} catch (InterruptedException e) {
			throw new CacheException(e);
		} catch (MemcachedException e) {
			throw new CacheException(e);
		}

	}

	public void close() throws CacheException {
		try {
			client.shutdown();
		} catch (IOException e) {
			throw new CacheException(e);
		}
	}

	private Map<String, String> getStats(String item) throws CacheException {
		Map<String, String> result = new HashMap<String, String>();
		try {
			Map<InetSocketAddress, Map<String, String>> statsMap = client.getStats();

			for (Entry<InetSocketAddress, Map<String, String>> stats : statsMap.entrySet()) {
				String host = stats.getKey().getAddress().getHostName();
				String value = stats.getValue().get(item);

				result.put(host, value);
			}
		} catch (MemcachedException e) {
			throw new CacheException(e);
		} catch (InterruptedException e) {
			throw new CacheException(e);
		} catch (TimeoutException e) {
			throw new CacheException(e);
		}

		return result;
	}

}
