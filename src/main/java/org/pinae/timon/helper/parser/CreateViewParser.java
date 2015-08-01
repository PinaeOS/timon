package org.pinae.timon.helper.parser;

import java.util.HashSet;
import java.util.Set;

import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.select.SelectBody;

public class CreateViewParser {
	// 数据表列表
	private Set<String> tableSet = new HashSet<String>();

	public Set<String> parse(CreateView createView) {
		if (createView != null) {
			SelectBody selectBody = createView.getSelectBody();
			if (selectBody != null) {
				tableSet.addAll(new SelectParser().parse(selectBody));
			}
		}
		return tableSet;
	}
}
