package org.pinae.timon.helper.parser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.update.Update;

public class UpdateParser extends SelectParser {

	public Set<String> parse(Update update) {
		
		Set<String> tableSet = new HashSet<String>();
		
		if (update != null) {
			List<Table> tables = update.getTables();
			for (Table table : tables) {
				tableSet.add(table.getName());
			}
			Expression expression = update.getWhere();
			if (expression != null) {
				tableSet.addAll(parseExpression(expression));
			}
		}
		return tableSet;
	}
}
