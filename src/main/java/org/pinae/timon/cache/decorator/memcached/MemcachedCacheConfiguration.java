package org.pinae.timon.cache.decorator.memcached;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.pinae.timon.cache.CacheConfiguration;
import org.pinae.timon.util.ConfigMap;

import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.utils.AddrUtil;

/**
 * memcached缓存配置信息
 * 
 * @author Huiyugeng
 *
 */
public class MemcachedCacheConfiguration extends CacheConfiguration {

	private String server; // 服务器地址

	private String authType = "md5"; // 认证类型

	private Map<String, String> auth; // 认证信息

	private String weights[]; // 权重信息

	private int poolSize = 3; // 连接池长度

	private boolean failureMode = false; // 主备模式

	/**
	 * 构造函数
	 */
	public MemcachedCacheConfiguration() {
		super();
	}

	/**
	 * 构造函数
	 * 
	 * @param config 配置条目
	 * 
	 */
	public MemcachedCacheConfiguration(ConfigMap<String, String> config) {
		super(config);
		
		setServer(config.getString("cache.memcached.server", "localhost:11211"));
		
		String authType = config.get("cache.memcached.auth_type");
		if (StringUtils.isNotBlank(authType)) {
			setAuthType(authType);
		}
		
		String auth = config.get("cache.memcached.auth");
		if (StringUtils.isNotBlank(auth)) {
			String auths[] = auth.split(";");
			Map<String, String> authMap = new HashMap<String, String>();
			for (String authItem : auths) {
				if (StringUtils.isNotBlank(authItem)) {
					String authPair[] = authItem.split("=");
					if (authPair != null && authPair.length == 2) {
						authMap.put(authPair[0].trim(), authPair[1].trim());
					}
				}
			}
			if (authMap.size() > 0) {
				setAuth(authMap);
			}
		}
		
		String weight = config.get("cache.memcached.weights");
		if (StringUtils.isNotBlank(weight)) {
			String weights[] = weight.split(";");
			setWeights(weights);
		}
		
		
		setPoolSize(config.getInteger("cache.memcached.pool_size", 5));
		setFailureMode(config.getBoolean("cache.memcached.failure_mode", false));
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public Map<InetSocketAddress, AuthInfo> getAuth() {
		Map<InetSocketAddress, AuthInfo> authMap = new HashMap<InetSocketAddress, AuthInfo>();
		if (auth != null && auth.size() > 0) {
			for (Entry<String, String> authEntry : auth.entrySet()) {
				String authInfo[] = authEntry.getValue().split("\\|");

				if (authInfo != null && authInfo.length == 2) {
					InetSocketAddress host = AddrUtil.getOneAddress(authEntry.getKey());
					if (authType.equals("md5")) {
						authMap.put(host, AuthInfo.cramMD5(authInfo[0], authInfo[1]));
					} else if (authType.equals("typecal")) {
						authMap.put(host, AuthInfo.typical(authInfo[0], authInfo[1]));
					} else {
						authMap.put(host, AuthInfo.plain(authInfo[0], authInfo[1]));
					}
				}

			}
		}
		return authMap;
	}

	public void setAuth(Map<String, String> auth) {
		this.auth = auth;
	}

	public int[] getWeights() {
		if (weights == null) {
			return null;
		}
		
		int result[] = new int[weights.length];

		for (int i = 0 ; i < weights.length; i++) {
			result[i] = 0;
			try {
				String weight = weights[i];
				if (StringUtils.isNotBlank(weight)) {
					result[i] = Integer.parseInt(weight.trim());
				} 
			} catch (NumberFormatException e) {

			}
		}
		return result;
	}

	public void setWeights(String[] weights) {
		this.weights = weights;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public boolean isFailureMode() {
		return failureMode;
	}

	public void setFailureMode(boolean failureMode) {
		this.failureMode = failureMode;
	}

}
