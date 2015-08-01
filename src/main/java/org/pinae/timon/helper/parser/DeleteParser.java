package org.pinae.timon.helper.parser;

import java.util.HashSet;
import java.util.Set;

import net.sf.jsqlparser.statement.delete.Delete;

public class DeleteParser {
	// 数据表列表
	private Set<String> tableSet = new HashSet<String>();

	public Set<String> parse(Delete delete) {
		if (delete != null) {
			tableSet.add(delete.getTable().getName().toUpperCase());
		}
		return tableSet;
	}
	
}
