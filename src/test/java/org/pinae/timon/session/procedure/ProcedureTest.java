package org.pinae.timon.session.procedure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.Test;
import org.pinae.timon.session.SqlSession;
import org.pinae.timon.session.SqlSessionFactory;
import org.pinae.timon.session.defaults.DefaultSqlSessionFactory;
import org.pinae.timon.sql.Sql;
import org.pinae.timon.sql.SqlBuilder;

public class ProcedureTest {
	private SqlBuilder builder = null;
	private SqlSessionFactory sessionFactory = null;
	
	public ProcedureTest() {
		try {
			this.builder = new SqlBuilder("sql/xml/sql_procedure_test.xml");
			this.sessionFactory = new DefaultSqlSessionFactory();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCall() throws IOException, SQLException {
		SqlSession session = sessionFactory.getSession();
		
		int count = 0;
		
		Sql sql = builder.getSQLByName("org.timon.test.procedure.COUNT_PERSON");
		Object result[] = session.call(sql);
		if (result != null && result.length > 0) {
			count = ((Integer)result[0]).intValue();
		} else {
			fail("COUNT_PERSON fail");
		}
		
		sql = builder.getSQLByName("org.timon.test.procedure.GET_PERSON");
		result = session.call(sql);
		if (result != null && result.length > 0) {
			assertEquals(count,  ((List<Object[]>)result[0]).size());
		} else {
			fail("GET_PERSON fail");
		}
	}
	

}
