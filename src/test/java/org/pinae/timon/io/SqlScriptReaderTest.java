package org.pinae.timon.io;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class SqlScriptReaderTest {
	
	@Test
	public void testGetSQLList() {
		SqlScriptReader reader = new SqlScriptReader();
		List<String> sqlList = reader.getSQLList("src/test/resources/test.sql", "UTF8");
		
		assertEquals(sqlList.size(), 8);
	}
}
