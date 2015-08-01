package org.pinae.timon.util;

import org.apache.commons.lang3.StringUtils;

/**
 * ClassLoader工具函数
 * 
 * @author 惠毓赓
 * 
 */
public class ClassLoaderUtils {
	private static ClassLoader systemClassLoader;

	static {
		systemClassLoader = ClassLoader.getSystemClassLoader();
	}

	/*
	 * 获取ClassLoader集合
	 */
	private static ClassLoader[] getClassLoaders(ClassLoader classLoader) {
		return new ClassLoader[] { classLoader, Thread.currentThread().getContextClassLoader(), ClassLoaderUtils.class.getClassLoader(),
				systemClassLoader };
	}

	/**
	 * 获取资源路径（classes根路径）
	 * 
	 * @param resource 资源
	 * @return 资源路径
	 */
	public static String getResourcePath(String resource) {
		String path = null;
		ClassLoader[] loaders = getClassLoaders(null);
		for (int i = 0; i < loaders.length; i++) {
			if (loaders[i] != null) {
				try {
					path = loaders[i].getResource(resource).getPath();
					if (StringUtils.isNotBlank(path)) {
						return path;
					}
				} catch (NullPointerException e) {

				}
			}
		}
		return path;
	}
}
