package org.pinae.timon.session.datasource;

import java.sql.Connection;

public interface DBConnection {
	
	public Connection getConnection();
}
