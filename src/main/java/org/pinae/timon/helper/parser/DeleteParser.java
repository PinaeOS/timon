package org.pinae.timon.helper.parser;

import java.util.HashSet;
import java.util.Set;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;

public class DeleteParser extends SelectParser {


	public Set<String> parse(Delete delete) {
		
		Set<String> tableSet = new HashSet<String>();
		
		if (delete != null) {
			Table table = delete.getTable();
			if (table != null) {
				tableSet.add(table.getName());
			}
			
			Expression expression = delete.getWhere();
			if (expression != null) {
				tableSet.addAll(parseExpression(expression));
			}
		}
		return tableSet;
	}
	
}
