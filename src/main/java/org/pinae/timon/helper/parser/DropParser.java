package org.pinae.timon.helper.parser;

import java.util.HashSet;
import java.util.Set;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.drop.Drop;

public class DropParser {

	public Set<String> parse(Drop drop) {
		
		Set<String> tableSet = new HashSet<String>();
		
		if (drop != null) {
			Table table = drop.getName();
			if (table != null) {
				tableSet.add(table.getName());
			}
		}
		return tableSet;
	}
}
