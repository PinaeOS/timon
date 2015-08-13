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
			this.builder = new SqlBuilder();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetSQLByName() {
		String sql = builder.getSQLByName("GET_ID");
		assertEquals(sql, "select id from person");
	}
	
	@Test
	public void testGetSQLByNameWithParameters1() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", 20);
		String sql = builder.getSQLByNameWithParameters("GET_PERSON_1", parameters);
		assertEquals(sql, "select * from person where 1=1 and id = 20");
	}
	
	@Test
	public void testGetSQLByNameWithParameters2() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", 20);
		String sql = builder.getSQLByNameWithParameters("GET_PERSON_2", parameters);
		assertEquals(sql, "select * from person where 1=1 and id = 20 order by id");
		
		parameters.put("name", "Hui");
		sql = builder.getSQLByNameWithParameters("GET_PERSON_2", parameters);
		assertEquals(sql, "select * from person where 1=1 and id = 20 and name = 'Hui' order by id");
	}
	
	@Test
	public void testGetSQLByNameWithObject() {

		Person person = new Person();
		person.setId(3);
		person.setName("Joe");
		person.setAge(20);
		person.setPhone("13391562775");
		
		String sql = builder.getSQLByNameWithParameters("INSERT_PERSON", person);
		assertEquals(sql, "insert into person(id, name, age, phone) values (3, 'Joe', 20, '13391562775')");
	}
	
	@Test
	public void testGetSQLByNameWithSubSQL() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", 20);
		String sql = builder.getSQLByNameWithParameters("SQL_REF_TEST", parameters);
		assertEquals(sql, "select name from (select name from (select name from USER1 where id = 20) t1 union select name from (select name from USER2 order by id) t2) t order by id");
	}
	
	@Test
	public void testGetLimitSQLForOracle() {
		String sql = builder.getSQLByName("GET_ID");
		sql = SqlBuilder.getLimitSQL(sql, 10, 10, "oracle");
		assertEquals(sql, "select * from ( select row_.*, rownum rownum_ from ( select id from person ) row_ where rownum <= 20) where rownum_ > 10");
	}
	
	@Test
	public void testGetLimitSQLForMySQL() {
		String sql = builder.getSQLByName("GET_ID");
		sql = SqlBuilder.getLimitSQL(sql, 10, 10, "mysql");
		assertEquals(sql, "select * from ( select id from person ) t limit 10, 10");
	}
	
	@Test
	public void testGetCountSQL() {
		String sql = builder.getSQLByName("GET_ID");
		sql = SqlBuilder.getCountSQL(sql);
		assertEquals(sql, "select count(*) from ( select id from person ) t");
	}
	
	@Test
	public void testGetScript() {
		List<String> sqlList = builder.getScript("TEST_SCRIPT");
		assertEquals(sqlList.size(), 8);
	}
	
}
