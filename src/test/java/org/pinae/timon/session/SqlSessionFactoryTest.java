package org.pinae.timon.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;
import org.pinae.timon.session.defaults.DefaultSqlSessionFactory;
import org.pinae.timon.session.handle.ConnectionHandler;

public class SqlSessionFactoryTest {

	@Test
	public void testGetSession() {
		SqlSessionFactory sessionFactory = null;
		
		try {
			sessionFactory = new DefaultSqlSessionFactory();
			
			SqlSession session = sessionFactory.getSession();
			assertEquals(session.isClosed(), false);
				
			session.close();
				
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetSessionWithHandler() {
		ConnectionHandler handler = new ConnectionHandler() {
			public void handle(Connection connection) {
				try {
					connection.setAutoCommit(false);
				} catch (SQLException e) {
					fail(e.getMessage());
				}
			}
		};
		
		SqlSessionFactory sessionFactory = null;
		
		try {
			sessionFactory = new DefaultSqlSessionFactory();
			
			SqlSession session = sessionFactory.getSession(handler);
			assertEquals(session.isClosed(), false);
			
			Connection connection = session.getConnection();
			assertEquals(connection.getAutoCommit(), false);
				
			session.close();
				
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (SQLException e) {
			fail(e.getMessage());
		}
	}
}
