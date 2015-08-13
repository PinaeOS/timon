package org.pinae.timon.reflection;

import java.util.ArrayList;
import java.util.List;

public class ReflectorData {
	public List<Object[]> getData() {
		List<Object[]> table = new ArrayList<Object[]>();
		
		table.add(new Object[]{1, "nala", 24, "13391562775"});
		table.add(new Object[]{2, "simba", 26, "13343351822"});
		table.add(new Object[]{3, "rafiki", 31, "13630183186"});
		table.add(new Object[]{4, "timon", 28, "13929977983"});
		
		return table;
	}
}