package org.pinae.timon.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

public class SQLSessionFactoryTest {

	@Test
	public void testGetSession() {
		SQLSessionFactory sessionFactory = null;
		
		try {
			sessionFactory = new SQLSessionFactory();
			
			SQLSession session = sessionFactory.getSession();
			assertEquals(session.isClosed(), false);
				
			session.close();
				
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
}
