package org.pinae.timon.mapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ObjectMapper implements Mapper {
	
	private static Logger log = Logger.getLogger(ObjectMapper.class);
	
	private Class<?> clazz;
	
	public ObjectMapper(Class<?> clazz) {
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
			
			for (int i = 0; i < columns.length; i++) {
				String columnName = (String) columns[i];

				Field field = clazz.getDeclaredField(columnName);
				field.setAccessible(true);
				field.set(object, row[i]);
			}
		} catch (Exception e) {
			log.error(String.format("ObjectMapper Exception: exception=%s", e.getMessage()));
		}

		return (T) object;
	}
}
