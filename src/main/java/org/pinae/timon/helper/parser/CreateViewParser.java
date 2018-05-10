package org.pinae.timon.helper.parser;

import java.util.HashSet;
import java.util.Set;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.select.SelectBody;

public class CreateViewParser {
	
	public Set<String> parse(CreateView createView) {
		Set<String> tableSet = new HashSet<String>();
		
		if (createView != null) {
			SelectBody selectBody = createView.getSelectBody();
			if (selectBody != null) {
				tableSet.addAll(new SelectParser().parse(selectBody));
			}
			Table view = createView.getView();
			if (view != null) {
				tableSet.add(view.getName());
			}
		}
		return tableSet;
	}
}
