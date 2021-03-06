package org.pinae.timon.reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.pinae.timon.reflection.annotation.Column;

public class AnnotationReflector implements Reflector {
	
	private static Logger logger = Logger.getLogger(AnnotationReflector.class);
	
	private Class<?> clazz;
	
	public AnnotationReflector(Class<?> clazz) {
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
		
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		if (columns != null && row != null) {
			for (int i = 0 ; i < columns.length; i++) {
				String column = columns[i];
				Object value = null;
				try {
					value = row[i];
				} catch (ArrayIndexOutOfBoundsException e) {
					
				}
				
				data.put(column, value);
			}
			
			try {
				object = clazz.newInstance();
			} catch (Exception e) {
				logger.error("Instance class exception: " + e.getMessage());
			}
			
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if(field.isAnnotationPresent(Column.class)) {
					Column column = (Column)field.getAnnotation(Column.class);
					
					if (data.containsKey(column.name())) {
						try {
							field.setAccessible(true);
							field.set(object, data.get(column.name()));
						} catch (Exception e) {
							logger.error("set field value exception: " + e.getMessage());
						}
					}
				}
			}
		}
			
		

		return (T) object;
	}

}
