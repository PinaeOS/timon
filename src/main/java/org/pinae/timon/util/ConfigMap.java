package org.pinae.timon.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * 配置Map
 * 
 * @author Huiyugeng
 *
 */
public class ConfigMap<K, V> extends HashMap<K, V> {
	
	private Logger logger = Logger.getLogger(ConfigMap.class);
	
	private static final long serialVersionUID = -2541287416226597213L;

	public String getString(String key, String defaultValue) {
		V value = get(key);
		if (value != null) {
			return value.toString();
		} else {
			return defaultValue;
		}
	}
	
	public Integer getInteger(String key, int defaultValue) {
		V value = get(key);
		try {
			if (value != null && StringUtils.isNumeric(value.toString())) {
				return Integer.parseInt(value.toString());
			} else {
				return defaultValue;
			}
		} catch (Exception e) {
			logger.error(String.format("parse exception: value=%s, defaultValue=%s", 
					value.toString(), defaultValue));
			return defaultValue;
		}
	}
	
	public Long getLong(String key, long defaultValue) {
		V value = get(key);
		try {
			if (value != null && StringUtils.isNumeric(value.toString())) {
				return Long.parseLong(value.toString());
			} else {
				return defaultValue;
			}
		} catch (Exception e) {
			logger.error(String.format("parse exception: value=%s, defaultValue=%s", 
					value.toString(), defaultValue));
			return defaultValue;
		}
	}
	
	public Short getShort(String key, short defaultValue) {
		V value = get(key);
		try {
			if (value != null && StringUtils.isNumeric(value.toString())) {
				return Short.parseShort(value.toString());
			} else {
				return defaultValue;
			}
		} catch (Exception e) {
			logger.error(String.format("parse exception: value=%s, defaultValue=%s", 
					value.toString(), defaultValue));
			return defaultValue;
		}
	}
	
	public Boolean getBoolean(String key, boolean defaultValue) {
		V value = get(key);
		try {
			if (value != null && StringUtils.isNumeric(value.toString())) {
				return Boolean.parseBoolean(value.toString());
			} else {
				return defaultValue;
			}
		} catch (Exception e) {
			logger.error(String.format("parse exception: value=%s, defaultValue=%s", 
					value.toString(), defaultValue));
			return defaultValue;
		}
	}
	
	public static ConfigMap<String, String> loadFromFile(String filename) throws IOException {
		
		ConfigMap<String, String> configMap = new ConfigMap<String, String>();
		
		try {
			Properties properties = new Properties();

			InputStream in = new BufferedInputStream(new FileInputStream(filename));
			properties.load(in);

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
			throw new IOException(String.format("Read %s error : %s", filename, e.getMessage()));
		}
		
		return configMap;
	}
}
