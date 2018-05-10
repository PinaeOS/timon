package org.pinae.timon.session;

import java.io.IOException;

import org.pinae.timon.session.defaults.DefaultSqlSessionFactory;

/**
 * 会话帮助类
 * 
 * @author Huiyugeng
 *
 */
public class SqlSessionHelper {
	
	private static final ThreadLocal<SqlSessionFactory> THREAD_SESSION_FACTORY = new ThreadLocal<SqlSessionFactory>();
	private static final ThreadLocal<SqlSession> THREAD_SESSION = new ThreadLocal<SqlSession>();
	
	/**
	 * 获取当前线程的会话工厂类
	 * 
	 * @return 会话工厂类
	 * @throws IOException IO异常处理
	 */
	public static SqlSessionFactory getSessionFactory() throws IOException {
		SqlSessionFactory sessionFactory = THREAD_SESSION_FACTORY.get();
		try {
			if (sessionFactory == null) {
				sessionFactory = new DefaultSqlSessionFactory();
				THREAD_SESSION_FACTORY.set(sessionFactory);
			}
		} catch (Exception e) {  
	        throw new IOException(e);  
	    }  
	    return sessionFactory;  
	}
	
	/**
	 * 获取当前线程的Sql会话
	 * 
	 * @return Sql会话类
	 * @throws IOException IO异常处理
	 */
	public static SqlSession getSession() throws IOException {
		SqlSession session = (SqlSession) THREAD_SESSION.get();  
	    try {  
	        if (session == null) {  
	        	session = getSessionFactory().getSession();
	        	THREAD_SESSION.set(session);  
	        }  
	    } catch (Exception e) {  
	        throw new IOException(e);  
	    }  
	    return session;  
	}
}
