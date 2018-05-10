package org.pinae.timon.io;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.pinae.timon.sql.io.SqlScriptReader;
import org.pinae.timon.util.FileUtils;

public class SqlScriptReaderTest {
	
	@Test
	public void testGetSQLList() {
		SqlScriptReader reader = new SqlScriptReader();
		List<String> sqlList = reader.getSQLList(FileUtils.getFileInputStream("test.sql"));
		
		assertEquals(sqlList.size(), 8);
	}
}
