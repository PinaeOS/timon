package org.pinae.timon.reflection;

import java.util.List;

public interface Reflector {
	
	public List<?> toList(List<Object[]> dataList, String[] columns);
	
	public <T> T toObject(Object[] row, String[] columns);
}
