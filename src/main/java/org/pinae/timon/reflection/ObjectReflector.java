package org.pinae.timon.reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ObjectReflector implements Reflector {
	
	private static Logger logger = Logger.getLogger(ObjectReflector.class);
	
	private Class<?> clazz;
	
	public ObjectReflector(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	public List<?> toList(List<Object[]> dataList, String[] columns) {

		List<Object> table = new ArrayList<Object>();
		
		if ((dataList != null && columns != null) && dataList.size() > 0) {
			for (Object[] row : dataList) {
				Object object = toObject(row, columns);
				table.add(object);
			}
		}
		return table;
	}

	@SuppressWarnings("unchecked")
	public <T> T toObject(Object[] row, String[] columns) {
		Object object = null;
		
		try {
			object = clazz.newInstance();
		} catch (Exception e) {
			logger.error("Instance class exception: " + e.getMessage());
		}
		
		for (int i = 0; i < columns.length; i++) {
			String columnName = (String) columns[i];
			try {
				Field field = clazz.getDeclaredField(columnName);
				field.setAccessible(true);
				field.set(object, row[i]);
			} catch (Exception e) {
				logger.error("set field value exception: " + e.getMessage());
			}
		}

		return (T) object;
	}
}
