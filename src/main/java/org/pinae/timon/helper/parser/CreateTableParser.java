package org.pinae.timon.helper.parser;

import java.util.HashSet;
import java.util.Set;

import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.Select;

public class CreateTableParser {
	// 数据表列表
	private Set<String> tableSet = new HashSet<String>();

	public Set<String> parse(CreateTable createTable) {
		if (createTable != null && createTable.getSelect() != null) {
			Select select = createTable.getSelect();
			tableSet.addAll(new SelectParser().parse(select));
		}
		return tableSet;
	}
}
