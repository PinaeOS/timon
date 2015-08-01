package org.pinae.timon.helper.parser;

import java.util.HashSet;
import java.util.Set;

import net.sf.jsqlparser.statement.insert.Insert;

public class InsertParser {
	// 数据表列表
	private Set<String> tableSet = new HashSet<String>();

	public Set<String> parse(Insert insert) {
		if (insert != null) {
			tableSet.add(insert.getTable().getName().toUpperCase());
		}
		return tableSet;
	}
}
