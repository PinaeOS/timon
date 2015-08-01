package org.pinae.timon.helper.parser;

import java.util.HashSet;
import java.util.Set;

import net.sf.jsqlparser.statement.drop.Drop;

public class DropParser {
	// 数据表列表
	private Set<String> tableSet = new HashSet<String>();

	public Set<String> parse(Drop drop) {
		if (drop != null) {
			tableSet.add(drop.getName());
		}
		return tableSet;
	}
}
