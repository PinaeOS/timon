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
	
	@Test
	public void testSelect() {
		List<Object[]> table = (List<Object[]>) session.select(builder.getSQLByName("GET_PERSON_1"));
		assertEquals(table.size(), 3); // 用户数量测试
		assertEquals(table.get(0)[0], 1); // 用户编号测试
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSelectForMap() {
		List<Map<String, Object>> table = (List<Map<String, Object>>) session.select(builder.getSQLByName("GET_PERSON_1"), Map.class);
		assertEquals(table.size(), 3); // 用户数量测试
		assertEquals(table.get(0).get("id"), 1); // 用户编号测试
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSelectForObject() {
		List<Person> table = (List<Person>) session.select(builder.getSQLByName("GET_PERSON_1"), Person.class);
		assertEquals(table.size(), 3); // 用户数量测试
		assertEquals(table.get(0).getId(), 1); // 用户编号测试
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSelectForAnnotation() {
		List<AnnotationPerson> table = (List<AnnotationPerson>) session.select(builder.getSQLByName("GET_PERSON_1"), AnnotationPerson.class);
		assertEquals(table.size(), 3); // 用户数量测试
		assertEquals(table.get(0).getUserId(), 1); // 用户编号测试
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSelectWithHandler() {
		// 当数据返回数量为3条时, 在获取的列表中删除第一个元素(id=1)
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
		assertEquals(table.get(0).get("id"), 2); // 用户编号测试
	}
	
	@Test
	public void testOne() {
		Object[] row = session.one(builder.getSQLByName("GET_ID"));
		assertEquals(row.length, 1);
		assertEquals(row[0], 1); // 用户编号测试
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
	
	@Test
	public void testGetColumnsBySql() {
		String[] columns = session.getColumnsBySql(builder.getSQLByName("GET_PERSON_1"));
		assertEquals(columns.length, 4);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCache() throws InterruptedException {
		/* 
		 * 在获取的列表(3条数据)中删除第一个元素, 缓存的数据应为2条, 
		 * 因此重新执行GET_USER_INFO_WITH_CACHE中的SQL, 即便没有加入ResultHandle, 
		 * 但是由于数据从缓存中获取, 因此获得的数据数量也是2条,
		 * 在SQL缓存过期后, 重新执行GET_USER_INFO_WITH_CACHE的SQL, 
		 * 在没有ResultHandle的情况下数据数量为3条
		*/
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
		
		String sql = builder.getSQLByName("org.timon.test.cache.GET_USER_INFO_WITH_CACHE");
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
