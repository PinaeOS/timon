package org.pinae.timon.session;

import java.io.IOException;

import org.pinae.timon.session.handle.ConnectionHandler;

public interface SqlSessionFactory {
	public SqlSession getSession() throws IOException;
	
	public SqlSession getSession(ConnectionHandler handler) throws IOException;
}
