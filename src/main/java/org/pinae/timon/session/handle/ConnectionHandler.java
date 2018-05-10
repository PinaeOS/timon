package org.pinae.timon.session.handle;

import java.sql.Connection;

/**
 * Connection处理
 * 
 * @author huiyugeng
 *
 */
public interface ConnectionHandler {
	public void handle(Connection connection);
}
