package org.pinae.timon.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

public class SQLSessionFactoryTest {

	@Test
	public void testGetSession() {
		SQLBuilder builder = null;
		
		try {
			builder = new SQLBuilder();
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		if (builder != null) {
			
			SQLSessionFactory sessionFactory = new SQLSessionFactory();
			SQLSession session = sessionFactory.getSession();
			
			assertEquals(session.isClosed(), false);
			
			session.close();
			
		} else {
			fail("SQLBuilder is null");
		}
	}
}
