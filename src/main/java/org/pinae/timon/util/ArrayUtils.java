package org.pinae.timon.util;

/**
 * 数组工具集
 * 
 * @author Hiyugeng
 *
 */
public class ArrayUtils {
	
	public static String[] toStringArray(Object[] array) {
		if (array == null)
			return null;
		String[] result = new String[array.length];
		for (int i = 0; i < result.length; i++) {
			if (array[i] != null) {
				result[i] = array[i].toString();
			}
		}
		return result;
	}
}
