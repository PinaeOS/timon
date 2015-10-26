package org.pinae.timon.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pinae.timon.session.defaults.DefaultSqlSessionFactory;
import org.pinae.timon.session.handle.ResultHandler;
import org.pinae.timon.session.pojo.AnnotationPerson;
import org.pinae.timon.session.pojo.Person;
import org.pinae.timon.sql.SqlBuilder;

public class SqlSessionTest {
	
	private static Logger logger = Logger.getLogger(SqlSessionTest.class);
	
	private static SqlBuilder builder = null;
	private static SqlSessionFactory sessionFactory = null;
	
	private SqlSession session = null;
	
	@BeforeClass
	public static void init() {
		try {
			SqlSessionTest.builder = new SqlBuilder();
			SqlSessionTest.sessionFactory = new DefaultSqlSessionFactory();
			
			List<String> initSqlList = SqlSessionTest.builder.getScript("INIT_SCRIPT");
			SqlSession session = sessionFactory.getSession();
			session.execute(initSqlList);
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
		logger.info("Create New Session:" + this.session.getConnection().toString());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSelectForMap() {
		List<Map<String, Object>> table = (List<Map<String, Object>>) session.select(builder.getSQLByName("GET_ID"), Map.class);
		assertEquals(table.size(), 3);
	}
	
	@SuppressWarnings("unchecked")
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
	
	@SuppressWarnings("unchecked")
	public void testSelectWithHandler() {
		ResultHandler handler = new ResultHandler() {
			@SuppressWarnings("rawtypes")
			public <T> void handle(T t) {
				if (t instanceof List) {
					List dataList = (List)t;
					if (dataList.size() == 3) {
						dataList.remove(0);
					}
				}
			}
		};
		List<Map<String, Object>> table = (List<Map<String, Object>>) session.select(builder.getSQLByName("GET_ID"), 
				Map.class, handler);
		assertEquals(table.size(), 2);
	}
	
	@Test
	public void testOneForAnnotationWithHandler() {
		ResultHandler handler = new ResultHandler() {
			public <T> void handle(T t) {
				if (t instanceof AnnotationPerson) {
					AnnotationPerson person = (AnnotationPerson)t;
					person.setUserName("Timon");
				}
			}
		};
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", 1);
		AnnotationPerson person = (AnnotationPerson)session.one(builder.getSQLByNameWithParameters("GET_PERSON_2", parameters), 
				AnnotationPerson.class, handler);
		assertEquals(person.getUserName(), "Timon");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCache() throws InterruptedException {
		ResultHandler handler = new ResultHandler() {
			@SuppressWarnings("rawtypes")
			public <T> void handle(T t) {
				if (t instanceof List) {
					List dataList = (List)t;
					if (dataList.size() == 3) {
						dataList.remove(0);
					}
				}
			}
		};
		
		String sql = builder.getSQLByName("org.timon.test.cache.GET_USER_INFO");
		List<Map<String, Object>> table = (List<Map<String, Object>>) session.select(sql, Map.class, handler);
		assertEquals(table.size(), 2);

		TimeUnit.SECONDS.sleep(5);

		table = (List<Map<String, Object>>) session.select(sql, Map.class);
		assertEquals(table.size(), 2);
		
		TimeUnit.SECONDS.sleep(10);
		
		table = (List<Map<String, Object>>) session.select(sql, Map.class);
		assertEquals(table.size(), 3);
	}
	
	@After
	public void close() {
		logger.info("Destory Session:" + this.session.getConnection().toString());
		this.session.close();
	}
}
