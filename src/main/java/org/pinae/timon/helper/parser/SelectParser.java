package org.pinae.timon.helper.parser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	// 数据表列表
	private Set<String> tableSet = new HashSet<String>();

	public Set<String> parse(Select select) {
		if (select != null) {
			parse(select.getSelectBody());
		}
		return tableSet;
	}

	public Set<String> parse(SelectBody selectBody) {
		if (selectBody != null) {
			if (selectBody instanceof PlainSelect) {
				PlainSelect plainSelect = (PlainSelect) selectBody;
				parseTable(plainSelect.getFromItem());
				
				List<Join> joins = plainSelect.getJoins();
				if (joins != null) {
					for (Join join : joins) {
						parseTable(join.getRightItem());
					}
				}
			}
	
			if (selectBody instanceof SetOperationList) {
				SetOperationList setList = (SetOperationList) selectBody;
				for (PlainSelect plainSelect : setList.getPlainSelects()) {
					parse(plainSelect);
				}
			}
		}
		return tableSet;
	}

	private void parseTable(FromItem fromItem) {
		if (fromItem instanceof Table) {
			Table table = (Table) fromItem;
			tableSet.add(table.getName().toUpperCase());
		} else if (fromItem instanceof SubSelect) {
			SubSelect subSelect = (SubSelect) fromItem;
			parse(subSelect.getSelectBody());
		}
	}

}
