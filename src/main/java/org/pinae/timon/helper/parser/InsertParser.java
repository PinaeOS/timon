package org.pinae.timon.helper.parser;

import java.util.HashSet;
import java.util.Set;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;

public class InsertParser {

	public Set<String> parse(Insert insert) {
		Set<String> tableSet = new HashSet<String>();
		
		if (insert != null) {
			Table table = insert.getTable();
			if (table != null) {
				tableSet.add(table.getName());
			}
		}
		return tableSet;
	}
}
