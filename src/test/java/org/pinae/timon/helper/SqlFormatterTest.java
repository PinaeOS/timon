package org.pinae.timon.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.pinae.timon.sql.Sql;
import org.pinae.timon.sql.SqlBuilder;
import org.pinae.timon.util.FileUtils;

public class SqlFormatterTest {
	private SqlBuilder builder;
	
	@Before
	public void before() {
		try{
			this.builder = new SqlBuilder(FileUtils.getFile("sql.xml"));
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testFormatSQL() {
		Sql sql = builder.getSQLByName("org.timon.test.format.formatSelectS1");
		
		SqlFormatter formatter = new SqlFormatter();
		String query = formatter.format(sql.getSql());
		
		String line[] = query.split("\n");
		assertEquals(line.length, 6);
		
		assertEquals(line[0].trim(), "select");
		assertEquals(line[1].trim(), "*");
		assertEquals(line[2].trim(), "from");
		assertEquals(line[3].trim(), "person");
		assertEquals(line[4].trim(), "where");
		assertEquals(line[5].trim(), "name='Hui'");
	}
}
