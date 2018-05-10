package org.pinae.timon.helper.parser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * Select语句解析器
 * 
 * @author Huiyugeng
 *
 */
public class SelectParser {

	public Set<String> parse(Select select) {
		if (select != null) {
			return parse(select.getSelectBody());
		}
		return null;
	}

	public Set<String> parse(SelectBody selectBody) {
		
		Set<String> tableSet = new HashSet<String>();
		
		if (selectBody != null) {
			if (selectBody instanceof PlainSelect) {
				PlainSelect plainSelect = (PlainSelect) selectBody;
				tableSet.addAll(parseTable(plainSelect.getFromItem()));
				
				List<Join> joins = plainSelect.getJoins();
				if (joins != null) {
					for (Join join : joins) {
						tableSet.addAll(parseTable(join.getRightItem()));
					}
				}
				
				Expression expression = plainSelect.getWhere();
				if (expression != null) {
					tableSet.addAll(parseExpression(expression));
				}
			}
	
			if (selectBody instanceof SetOperationList) {
				SetOperationList setList = (SetOperationList) selectBody;
				for (SelectBody select : setList.getSelects()) {
					tableSet.addAll(parse(select));
				}
			}
			
		}
		return tableSet;
	}
	
	protected Set<String> parseExpression(Expression expression) {
		Set<String> expTableSet = new HashSet<String>();
		if (expression instanceof BinaryExpression) {
			expTableSet.addAll(parseExpression(((BinaryExpression)expression).getLeftExpression()));
			expTableSet.addAll(parseExpression(((BinaryExpression)expression).getRightExpression()));
		}
		
		if (expression instanceof InExpression) {
			ItemsList itemList = ((InExpression)expression).getRightItemsList();
			if (itemList instanceof SubSelect) {
				SubSelect subSelect = (SubSelect)itemList;
				expTableSet.addAll(parse(subSelect.getSelectBody()));
			}
		}
		
		return expTableSet;
	}

	private Set<String> parseTable(FromItem fromItem) {
		Set<String> tableSet = new HashSet<String>();
		if (fromItem instanceof Table) {
			Table table = (Table) fromItem;
			tableSet.add(table.getName());
		} else if (fromItem instanceof SubSelect) {
			SubSelect subSelect = (SubSelect) fromItem;
			tableSet.addAll(parse(subSelect.getSelectBody()));
		}
		return tableSet;
	}

}
