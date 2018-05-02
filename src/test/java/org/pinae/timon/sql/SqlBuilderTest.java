package org.pinae.timon.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.pinae.timon.session.pojo.Person;
import org.pinae.timon.sql.SqlBuilder;


public class SqlBuilderTest {
	
	private SqlBuilder builder;
	
	@Before
	public void before() {
		try {
			this.builder = new SqlBuilder("sql.xml");
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetSQLByName() {
		Sql sql = builder.getSQLByName("org.timon.test.builder.GET_ID");
		assertEquals(sql.getSql(), "select id from person");
	}
	
	@Test
	public void testGetSQLByNameWithParameters1() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", 20);
		Sql sql = builder.getSQLByNameWithParameters("org.timon.test.builder.GET_PERSON_1", parameters);
		assertEquals(sql.getSql(), "select * from person where 1=1 and id = 20");
	}
	
	@Test
	public void testGetSQLByNameWithParameters2() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", 20);
		Sql sql = builder.getSQLByNameWithParameters("org.timon.test.builder.GET_PERSON_2", parameters);
		assertEquals(sql.getSql(), "select * from person where 1=1 and id = 20 order by id");
		
		parameters.put("name", "Hui");
		sql = builder.getSQLByNameWithParameters("org.timon.test.builder.GET_PERSON_2", parameters);
		assertEquals(sql.getSql(), "select * from person where 1=1 and name like 'Hui' and id = 20 order by id");
	}
	
	@Test
	public void testGetSQLByNameWithObject() {

		Person person = new Person();
		person.setId(3);
		person.setName("Joe");
		person.setAge(20);
		person.setPhone("13391562775");
		
		Sql sql = builder.getSQLByNameWithParameters("org.timon.test.builder.INSERT_PERSON", person);
		assertEquals(sql.getSql(), "insert into person(id, name, age, phone) values (3, 'Joe', 20, '13391562775')");
	}
	
	@Test
	public void testGetSQLByNameWithSubSQL() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", 20);
		Sql sql = builder.getSQLByNameWithParameters("org.timon.test.builder.SQL_REF_TEST", parameters);
		assertEquals(sql.getSql(), "select name from (select name from (select name from USER1 where id = 20) t1 union select name from (select name from USER2 order by id) t2) t order by id");
	}
	
	@Test
	public void testGetLimitSQLForOracle() {
		Sql sql = builder.getSQLByName("org.timon.test.builder.GET_ID");
		String query = SqlBuilder.getLimitSQL(sql.getSql(), 10, 10, "oracle");
		assertEquals(query, "select * from ( select row_.*, rownum rownum_ from ( select id from person ) row_ where rownum <= 20) where rownum_ > 10");
	}
	
	@Test
	public void testGetLimitSQLForMySQL() {
		Sql sql = builder.getSQLByName("org.timon.test.builder.GET_ID");
		String query = SqlBuilder.getLimitSQL(sql.getSql(), 10, 10, "mysql");
		assertEquals(query, "select * from ( select id from person ) t limit 10, 10");
	}
	
	@Test
	public void testGetCountSQL() {
		Sql sql = builder.getSQLByName("org.timon.test.builder.GET_ID");
		String query = SqlBuilder.getCountSQL(sql.getSql());
		assertEquals(query, "select count(*) from ( select id from person ) t");
	}
	
	@Test
	public void testGetScript() {
		List<String> sqlList = builder.getScript("TEST_SCRIPT");
		assertEquals(sqlList.size(), 8);
	}
	
}
