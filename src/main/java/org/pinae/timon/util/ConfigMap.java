package org.pinae.timon.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * 配置信息Map
 * 
 * @author Huiyugeng
 *
 * @param <K> Map键类型
 * @param <V> Map值类型
 */
public class ConfigMap<K, V> extends HashMap<K, V> {
	
	private static final long serialVersionUID = 7786845536669625320L;
	
	private Logger logger = Logger.getLogger(ConfigMap.class);

	public ConfigMap() {

	}

	public ConfigMap(Map<? extends K, ? extends V> map) {
		super(map);
	}
	
	@SuppressWarnings("unchecked")
	public ConfigMap(Object object) {
		if (object != null) {
			if (object instanceof Map) {
				try {
					putAll((Map<? extends K, ? extends V>) object);
				} catch (Exception e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
	}
	
	public ConfigMap<K, V> getMap(K key) {
		if (containsKey(key)) {
			V v = get(key);
			if (v instanceof Map) {
				ConfigMap<K, V> subMap = new ConfigMap<K, V>(v);
				return subMap;
			}
		}
		return null;
	}

	public String getString(K key, String defaultValue) {
		V v = get(key);
		if (v == null) {
			return defaultValue;
		}
		return v.toString();
	}

	public long getLong(K key, long defaultValue) {
		V v = get(key);
		if (v != null) {
			try {
				return Long.parseLong(v.toString());
			} catch (NumberFormatException e) {
				logger.error(String.format("parse exception: value=%s, defaultValue=%s", 
						v.toString(), defaultValue));
			}
		}
		return defaultValue;
	}

	public int getInteger(K key, int defaultValue) {
		V v = get(key);
		if (v != null) {
			try {
				return Integer.parseInt(v.toString());
			} catch (NumberFormatException e) {
				logger.error(String.format("parse exception: value=%s, defaultValue=%s", 
						v.toString(), defaultValue));
			}
		}
		return defaultValue;
	}

	public double getDouble(K key, double defaultValue) {
		V v = get(key);
		if (v != null) {
			try {
				return Double.parseDouble(v.toString());
			} catch (NumberFormatException e) {
				logger.error(String.format("parse exception: value=%s, defaultValue=%s", 
						v.toString(), defaultValue));
			}
		}
		return defaultValue;
	}
	
	public Short getShort(K key, short defaultValue) {
		V v = get(key);
		try {
			if (v != null && StringUtils.isNumeric(v.toString())) {
				return Short.parseShort(v.toString());
			} else {
				return defaultValue;
			}
		} catch (Exception e) {
			logger.error(String.format("parse exception: value=%s, defaultValue=%s", 
					v.toString(), defaultValue));
			return defaultValue;
		}
	}

	public boolean getBoolean(K key, boolean defaultValue) {
		V v = get(key);
		if (v != null) {
			return Boolean.parseBoolean(v.toString().toLowerCase());
		}
		return defaultValue;
	}

	public boolean isNotBlank(K key) {
		V v = get(key);
		if (v == null) {
			return false;
		}
		return StringUtils.isNotBlank(v.toString());
	}

	public boolean equals(K key, V object) {
		V v = get(key);
		if (v == null && object == null) {
			return true;
		}
		if (v != null && object != null) {
			return v.equals(object);
		}
		return false;
	}

	public boolean equalsIgnoreCase(K key, V object) {
		V v = get(key);
		if (v == null && object == null) {
			return true;
		}
		if (v != null && object != null) {
			return v.toString().equalsIgnoreCase(object.toString());
		}
		return false;
	}
	
	public static ConfigMap<String, String> load(File file) throws IOException {
		
		if (file == null) {
			throw new IOException("Properties file is NULL");
		}
		
		if (!file.exists() || !file.isFile()) {
			throw new IOException(file.getAbsolutePath() + " is NOT exists or NOT a file");
		}
		
		ConfigMap<String, String> configMap = null;
		
		try {
			Properties properties = new Properties();
			// 读取Properties配置文件
			InputStream in = new BufferedInputStream(new FileInputStream(file));
			properties.load(in);

			configMap = new ConfigMap<String, String>();
			
			Set<Object> propKeySet = properties.keySet();
			for (Object propKey : propKeySet) {
				if (propKeySet != null) {
					String key = propKey.toString();
					String value = properties.getProperty(key);
					configMap.put(key, value);
				}
			}

			IOUtils.closeQuietly(in);

		} catch (Exception e) {
			throw new IOException(String.format("Read %s error : %s", file.getAbsolutePath(), e.getMessage()));
		}
		
		return configMap;
	}

}