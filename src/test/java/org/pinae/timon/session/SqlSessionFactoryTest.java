package org.pinae.timon.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;
import org.pinae.timon.session.defaults.DefaultSqlSessionFactory;
import org.pinae.timon.session.handle.ConnectionHandler;
import org.pinae.timon.util.ConfigMap;

public class SqlSessionFactoryTest {
	
	@Test
	public void testTestConnection() {
		try {
			SqlSessionFactory sessionFactory = new DefaultSqlSessionFactory();
			assertTrue(sessionFactory.testConnection());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		try {
			SqlSessionFactory sessionFactory = new DefaultSqlSessionFactory("jdbc", "com.mysql.jdbc.Driver", 
					"jdbc:mysql://127.0.0.1:3306/test", "test", "test");
			assertTrue(sessionFactory.testConnection());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		try {
			ConfigMap<String, String> datasource = new ConfigMap<String, String>();
			datasource.put("type", "jdbc");
			datasource.put("driver", "com.mysql.jdbc.Driver");
			datasource.put("url", "jdbc:mysql://127.0.0.1:3306/test");
			datasource.put("user", "test");
			datasource.put("password", "test");
			SqlSessionFactory sessionFactory = new DefaultSqlSessionFactory(datasource);
			assertTrue(sessionFactory.testConnection());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

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
