package org.pinae.timon.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.pinae.timon.session.SQLBuilder;

public class SQLFormatterTest {
	private SQLBuilder builder;
	
	@Before
	public void before() {
		try{
			this.builder = new SQLBuilder();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testFormatSQL() {
		String sql = builder.getSQLByName("org.timon.test.foramt.formatSelectS1");
		
		SQLFormatter formatter = new SQLFormatter();
		sql = formatter.format(sql);
		
		String line[] = sql.split("\n");
		assertEquals(line.length, 6);
		
		assertEquals(line[0].trim(), "select");
		assertEquals(line[1].trim(), "*");
		assertEquals(line[2].trim(), "from");
		assertEquals(line[3].trim(), "person");
		assertEquals(line[4].trim(), "where");
		assertEquals(line[5].trim(), "name='Hui'");
	}
}
