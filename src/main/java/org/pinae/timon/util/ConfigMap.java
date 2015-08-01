package org.pinae.timon.util;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * 配置MAP
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
}
