package org.pinae.timon.session;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.BeforeClass;
import org.pinae.timon.session.defaults.DefaultSqlSessionFactory;

public class SqlSessionWithJDBCTest extends SqlSessionTest {
	
	@BeforeClass
	public static void initFactory() throws SQLException {
		try {
			SqlSessionTest.sessionFactory = new DefaultSqlSessionFactory();
			// 数据初始化
			initData();
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
}
