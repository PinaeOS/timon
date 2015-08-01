package org.pinae.timon.helper.parser;

import java.util.HashSet;
import java.util.Set;

import net.sf.jsqlparser.statement.alter.Alter;

public class AlterParser {
	// 数据表列表
	private Set<String> tableSet = new HashSet<String>();

	public Set<String> parse(Alter alter) {
		if (alter != null) {
			tableSet.add(alter.getTable().getName());
		}
		return tableSet;
	}
}
