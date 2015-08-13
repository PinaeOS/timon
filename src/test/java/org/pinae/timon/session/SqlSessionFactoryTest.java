package org.pinae.timon.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

public class SqlSessionFactoryTest {

	@Test
	public void testGetSession() {
		SqlSessionFactory sessionFactory = null;
		
		try {
			sessionFactory = new SqlSessionFactory();
			
			SqlSession session = sessionFactory.getSession();
			assertEquals(session.isClosed(), false);
				
			session.close();
				
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
}
