package org.pinae.timon.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pinae.timon.session.pojo.AnnotationPerson;
import org.pinae.timon.session.pojo.Person;

public class SQLSessionTest {
	
	private static Logger log = Logger.getLogger(SQLSessionTest.class);
	
	private static SQLBuilder builder = null;
	private static SQLSessionFactory sessionFactory = null;
	
	private SQLSession session = null;
	
	@BeforeClass
	public static void init() {
		try {
			SQLSessionTest.builder = new SQLBuilder();
			SQLSessionTest.sessionFactory = new SQLSessionFactory();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Before
	public void before() {
		try {
			this.session = sessionFactory.getSession();
		} catch (IOException e) {
			fail(e.getMessage());
		}
		log.info("Create New Session:" + this.session.getConnection().toString());
	}
	
	@Test
	public void testSelectForMap() {
		List<Map<String, Object>> table = (List<Map<String, Object>>) session.select(builder.getSQLByName("GET_ID"), Map.class);
		assertEquals(table.size(), 3);
	}
	
	@Test
	public void testOneForMap() {
		Map<String, Object> firstRow = (Map<String, Object>)session.one(builder.getSQLByName("GET_ID"), Map.class);
		assertEquals(firstRow.size(), 1);
	}
	
	@Test
	public void testOneForObject() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", 1);
		Person person = (Person)session.one(builder.getSQLByNameWithParameters("GET_PERSON_1", parameters), Person.class);
		assertNotNull(person.getName());
	}
	
	@Test
	public void testOneForAnnotation() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", 1);
		AnnotationPerson person = (AnnotationPerson)session.one(builder.getSQLByNameWithParameters("GET_PERSON_2", parameters), AnnotationPerson.class);
		assertNotNull(person.getUserName());
	}
	
	@After
	public void close() {
		log.info("Destory Session:" + this.session.getConnection().toString());
		this.session.close();
	}
}
