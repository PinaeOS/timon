package org.pinae.timon.mapper;

import java.util.List;

public interface Mapper {
	
	public List<?> toList(List<Object[]> dataList, String[] columns);
	
	public <T> T toObject(Object[] row, String[] columns);
}
