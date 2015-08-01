package org.pinae.timon.helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.pinae.timon.mapper.MapMapper;
import org.pinae.timon.session.SQLMetaData;
import org.pinae.timon.session.SQLSession;

/**
 * 样本数据载入
 * 
 * @author Huiyugeng
 * 
 */
public class SampleDataLoader {
	
	private SQLSession session = null;
	private SQLMetaData metadata = null;
	
	public SampleDataLoader(SQLSession session, SQLMetaData metadata) {
		this.session = session;
		this.metadata = metadata;
	}
	
	/**
	 * 根据数据表获取样本数据
	 * 
	 * @param table 数据表
	 * @param columns 数据字段
	 * 
	 * @return 样本数据列表
	 */
	public List<Map<String, Object>> loadSampleDataByTable(String table, Iterator<String> columns) {
		if (columns == null || StringUtils.isEmpty(table)) {
			return null;
		}
		String sql = String.format("select %s from %s", StringUtils.join(columns, " ,"), table);
		return loadSampleDataBySql(sql);
	}

	/**
	 * 根据SQL获取样本数据
	 * 
	 * @param datasource 数据源
	 * @param sql SQL语句
	 * 
	 * @return 样本数据列表
	 */
	public List<Map<String, Object>> loadSampleDataBySql(String sql) {

		List<Map<String, Object>> result = null;
		
		if (session != null && metadata != null) {
			String columns[] = metadata.getColumnsBySQL(sql);
			List<Object[]> table = session.select(sql);
			result = new MapMapper().toMapList(table, columns);
		}
		
		session.close();

		return result;
	}

}
