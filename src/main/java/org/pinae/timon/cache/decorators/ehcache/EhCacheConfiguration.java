package org.pinae.timon.cache.decorators.ehcache;

import org.pinae.timon.cache.CacheConfiguration;
import org.pinae.timon.util.ConfigMap;

/**
 * 基于Ehcache的缓存配置
 * 
 * @author Huiyugeng
 *
 */
public class EhCacheConfiguration extends CacheConfiguration {
	
	private String configUrl = null; // 配置文件URL
	
	private String evictionPolicy; // 对象回收策略
	
	/**
	 * 构造函数
	 */
	public EhCacheConfiguration() {
		super();
	}

	/**
	 * 构造函数
	 * 
	 * @param config 配置条目
	 * 
	 */
	public EhCacheConfiguration(ConfigMap<String, String> config) {
		super(config);
		
		this.configUrl = config.get("cache.ehcache.url");
		this.evictionPolicy = config.getString("cache.ehcache.eviction", "LFU").toUpperCase();
	}

	/**
	 * 返回配置文件URL
	 * 
	 * @return 配置文件URL
	 */
	public String getConfigUrl() {
		return configUrl;
	}

	/**
	 * 设置配置文件URL
	 * 
	 * @param configUrl 配置文件URL
	 */
	public void setConfigUrl(String configUrl) {
		this.configUrl = configUrl;
	}

	/**
	 * 返回对象回收策略
	 * 
	 * @return 对象回收策略
	 */
	public String getEvictionPolicy() {
		if (evictionPolicy == null) {
			return "LFU";
		}
		return evictionPolicy;
	}

	/**
	 * 设置对象回收策略
	 * 
	 * @param evictionPolicy 对象回收策略 (LFU / LRU / FIFO / CLOCK )
	 */
	public void setEvictionPolicy(String evictionPolicy) {
		this.evictionPolicy = evictionPolicy;
	}	

}
