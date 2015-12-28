package org.pinae.timon.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import org.pinae.timon.sql.Sql;
import org.pinae.timon.sql.SqlBuilder;

public class SqlParserTest {
	
	private SqlBuilder builder;
	
	private SqlParser parser = new SqlParser();
	
	@Before
	public void before() {
		try{
			this.builder = new SqlBuilder();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testParserSelect1() {
		Sql sql = builder.getSQLByName("org.timon.test.parser.parseSelectS1");
		
		Set<String> tableSet = parser.getTable(sql.getSql());
		assertEquals(tableSet.size(), 1);
		assertTrue(tableSet.contains("PERSON"));

	}
	
	@Test
	public void testParserSelect2() {
		Sql sql = builder.getSQLByName("org.timon.test.parser.parseSelectS2");
		
		Set<String> tableSet = parser.getTable(sql.getSql());
		assertEquals(tableSet.size(), 2);
		assertTrue(tableSet.contains("PERSON"));
		assertTrue(tableSet.contains("DEPARTMENT"));
	}
	
	@Test
	public void testParserSelect3() {
		Sql sql = builder.getSQLByName("org.timon.test.parser.parseSelectS3");
		
		Set<String> tableSet = parser.getTable(sql.getSql());
		assertEquals(tableSet.size(), 3);
		assertTrue(tableSet.contains("USER"));
		assertTrue(tableSet.contains("DEPARTMENT"));
		assertTrue(tableSet.contains("USER_TEMP"));
	}
	
	@Test
	public void testParserInsert1() {
		Sql sql = builder.getSQLByName("org.timon.test.parser.parseInsertS1");
		
		Set<String> tableSet = parser.getTable(sql.getSql());
		assertEquals(tableSet.size(), 1);
		assertTrue(tableSet.contains("PERSON"));
	}
	
	@Test
	public void testParserInsert2() {
		Sql sql = builder.getSQLByName("org.timon.test.parser.parseInsertS2");
		
		Set<String> tableSet = parser.getTable(sql.getSql());
		assertEquals(tableSet.size(), 1);
		assertTrue(tableSet.contains("PERSON_BAK"));
	}
	
	@Test
	public void testParserDelete1() {
		Sql sql = builder.getSQLByName("org.timon.test.parser.parseDeleteS1");
		
		Set<String> tableSet = parser.getTable(sql.getSql());
		assertEquals(tableSet.size(), 2);
		assertTrue(tableSet.contains("PERSON"));
		assertTrue(tableSet.contains("DEPARTMENT"));
	}
	
	@Test
	public void testParserUpdate1() {
		Sql sql = builder.getSQLByName("org.timon.test.parser.parseDeleteS1");
		
		Set<String> tableSet = parser.getTable(sql.getSql());
		assertEquals(tableSet.size(), 2);
		assertTrue(tableSet.contains("PERSON"));
		assertTrue(tableSet.contains("DEPARTMENT"));
	}
	
	@Test
	public void testParseCreateTableS1() {
		Sql sql = builder.getSQLByName("org.timon.test.parser.parseCreateTableS1");
		
		Set<String> tableSet = parser.getTable(sql.getSql());
		assertEquals(tableSet.size(), 1);
		assertTrue(tableSet.contains("PERSON"));
	}
	
	@Test
	public void testParseCreateTableS2() {
		Sql sql = builder.getSQLByName("org.timon.test.parser.parseCreateTableS2");
		
		Set<String> tableSet = parser.getTable(sql.getSql());
		assertEquals(tableSet.size(), 2);
		assertTrue(tableSet.contains("PERSON_TMP"));
		assertTrue(tableSet.contains("PERSON"));
	}
	
	@Test
	public void testParseCreateViewS1() {
		Sql sql = builder.getSQLByName("org.timon.test.parser.parseCreateViewS1");
		
		Set<String> tableSet = parser.getTable(sql.getSql());
		assertEquals(tableSet.size(), 3);
		assertTrue(tableSet.contains("VIEW_PERSON"));
		assertTrue(tableSet.contains("PERSON"));
		assertTrue(tableSet.contains("DEPARTMENT"));
	}
	
	@Test
	public void testParseAlertS1() {
		Sql sql = builder.getSQLByName("org.timon.test.parser.parseAlertS1");
		
		Set<String> tableSet = parser.getTable(sql.getSql());
		assertEquals(tableSet.size(), 1);
		assertTrue(tableSet.contains("PERSON"));
	}

	@Test
	public void testParseDropS1() {
		Sql sql = builder.getSQLByName("org.timon.test.parser.parseDropS1");
		
		Set<String> tableSet = parser.getTable(sql.getSql());
		assertEquals(tableSet.size(), 1);
		assertTrue(tableSet.contains("PERSON"));
	}

}
