package org.pinae.timon.io;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class SQLScriptReaderTest {
	
	@Test
	public void testGetSQLList() {
		SQLScriptReader reader = new SQLScriptReader();
		List<String> sqlList = reader.getSQLList("src/test/java/test.sql", "UTF8");
		
		assertEquals(sqlList.size(), 3);
	}
}
