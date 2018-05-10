package org.pinae.timon.reflection;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class MapReflectorTest {
	
	@Test
	public void testToList() {
		MapReflector refector = new MapReflector();
		List<Map<String, Object>> people = (List<Map<String, Object>>) refector.toList(ReflectorData.getData(),
				new String[] { "id", "name", "age", "phone" });
		
		assertEquals(people.size(), 4);
		
		assertEquals(people.get(0).get("name"), "nala");
		assertEquals(people.get(0).get("phone"), "1392997798");
		assertEquals(people.get(0).get("age"), 28);
		
		assertEquals(people.get(1).get("name"), "simba");
		assertEquals(people.get(1).get("phone"), "13630183186");
		assertEquals(people.get(1).get("age"), 31);
	}
}
