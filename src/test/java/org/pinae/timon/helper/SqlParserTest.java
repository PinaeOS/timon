package org.pinae.timon.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.pinae.timon.helper.parser.SelectParser;
import org.pinae.timon.sql.Sql;
import org.pinae.timon.sql.SqlBuilder;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;

public class SqlParserTest {
	
	private SqlBuilder builder;
	
	@Before
	public void before() {
		try{
			this.builder = new SqlBuilder();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testParserSQL() {
		Sql sql = builder.getSQLByName("org.timon.test.parser.parseSelectS1");
		
		try {
			Statement statement = new CCJSqlParserManager().parse(new StringReader(sql.getSql()));
			if (statement instanceof Select) {
				Set<String> tableSet = new SelectParser().parse((Select)statement);
				assertEquals(tableSet.size(), 3);
			}
		} catch (JSQLParserException e) {
			fail(e.getMessage());
		}
	}
}
