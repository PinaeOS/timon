package org.pinae.timon.session;

import java.io.IOException;

import org.pinae.timon.session.handle.ConnectionHandler;

/**
 * 数据库会话工厂
 * 
 * @author Huiyugeng
 *
 */
public interface SqlSessionFactory {
	
	/**
	 * 数据库连接测试
	 * 
	 * @return 是否连接成功
	 */
	public boolean testConnection();
	
	/**
	 * 获取数据库会话
	 * 
	 * @return 数据库会话
	 * 
	 * @throws IOException 数据库IO异常
	 */
	public SqlSession getSession() throws IOException;
	
	/**
	 * 获取数据库会话
	 * 
	 * @param handler 连接处理类
	 * @return 数据库会话
	 * 
	 * @throws IOException 数据库IO异常
	 */
	public SqlSession getSession(ConnectionHandler handler) throws IOException;
}
