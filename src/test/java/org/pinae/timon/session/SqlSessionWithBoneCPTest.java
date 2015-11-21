package org.pinae.timon.session;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.BeforeClass;
import org.pinae.timon.session.defaults.DefaultSqlSessionFactory;
import org.pinae.timon.util.ClassLoaderUtils;

public class SqlSessionWithBoneCPTest extends SqlSessionTest {

	@BeforeClass
	public static void initFactory() {
		
		String path = ClassLoaderUtils.getResourcePath("");
		
		try {
			SqlSessionTest.sessionFactory = new DefaultSqlSessionFactory(path + "database_bonecp.properties");
			// 数据初始化
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

}
