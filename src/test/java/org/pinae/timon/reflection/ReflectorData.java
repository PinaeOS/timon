package org.pinae.timon.reflection;

import java.util.ArrayList;
import java.util.List;

public class ReflectorData {
	public static List<Object[]> getData() {
		List<Object[]> table = new ArrayList<Object[]>();
		
		table.add(new Object[]{1, "nala", 28, "1392997798"});
		table.add(new Object[]{2, "simba", 31, "13630183186"});
		table.add(new Object[]{3, "rafiki", 22, "13391562775"});
		table.add(new Object[]{4, "timon", 18, "13343351822"});
		
		return table;
	}
}