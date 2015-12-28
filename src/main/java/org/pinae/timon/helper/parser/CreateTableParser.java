package org.pinae.timon.helper.parser;

import java.util.HashSet;
import java.util.Set;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.Select;

public class CreateTableParser {

	public Set<String> parse(CreateTable createTable) {
		Set<String> tableSet = new HashSet<String>();
		
		if (createTable != null) {
			Table table = createTable.getTable();
			if (table != null) {
				tableSet.add(table.getName());
			}
			Select select = createTable.getSelect();
			if (select != null) {
				tableSet.addAll(new SelectParser().parse(select));
			}
		}
		return tableSet;
	}
}
