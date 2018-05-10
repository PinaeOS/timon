package org.pinae.timon.session.datasource;

import java.io.IOException;
import java.sql.Connection;

public interface DataSource {
	public Connection getConnection() throws IOException;
}
