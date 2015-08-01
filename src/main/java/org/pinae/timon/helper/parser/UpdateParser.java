package org.pinae.timon.helper.parser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.update.Update;

public class UpdateParser {
	// 数据表列表
	private Set<String> tableSet = new HashSet<String>();

	public Set<String> parse(Update update) {
		if (update != null) {
			List<Table> tables = update.getTables();
			for (Table table : tables) {
				tableSet.add(table.getName().toUpperCase());
			}
		}
		return tableSet;
	}
}
