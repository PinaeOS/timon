package org.pinae.timon.mapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ObjectMapper {
	/**
	 * 将查询结果映射成为List<对象>
	 * 
	 * @param table 查询结果
	 * @param columns 列名
	 * @param clazz 映射的类名称
	 * 
	 * @return 映射后的List<对象>
	 */
	public List<?> toObjectList(List<Object[]> table, String[] columns, Class<?> clazz) {

		List<Object> resultList = new ArrayList<Object>();
		if ((table != null && columns != null) && table.size() > 0) {

			for (Object[] row : table) {
				Object object = null;
				try {
					object = clazz.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}

				for (int i = 0; i < columns.length; i++) {

					String columnName = (String) columns[i];
					try {
						Field field = clazz.getDeclaredField(columnName);
						field.setAccessible(true);
						field.set(object, row[i]);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				resultList.add(object);
			}

		}
		return resultList;
	}
}
