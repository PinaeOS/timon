package org.pinae.timon.helper.parser;

import java.util.HashSet;
import java.util.Set;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.alter.Alter;

public class AlterParser {

	public Set<String> parse(Alter alter) {
		Set<String> tableSet = new HashSet<String>();
		
		if (alter != null) {
			Table table = alter.getTable();
			if (table != null) {
				tableSet.add(table.getName());
			}
		}
		return tableSet;
	}
}
