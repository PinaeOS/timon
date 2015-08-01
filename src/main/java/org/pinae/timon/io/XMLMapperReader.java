package org.pinae.timon.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.pinae.nala.xb.exception.NoSuchPathException;
import org.pinae.nala.xb.exception.UnmarshalException;
import org.pinae.nala.xb.unmarshal.Unmarshaller;
import org.pinae.nala.xb.unmarshal.XMLUnmarshaller;
import org.pinae.nala.xb.util.ResourceReader;
import org.pinae.timon.io.SQLMapper.GlobalVar;
import org.pinae.timon.io.SQLMapper.Import;
import org.pinae.timon.io.SQLMapper.SQL;

/**
 * 从文件中读取SQL配置
 * 
 * @author huiyugeng
 *
 */
public class XMLMapperReader {
	
	/**
	 * 获取SQL列表
	 * 
	 * @param filename SQL文件
	 * @throws IOException 
	 */
	public static Map<String, SQL> read(String path, String filename) throws IOException {
		return read(path, filename, null);
	}


	public static Map<String, SQL> read(String path, String filename, Map<String, String> globalVar) throws IOException {
		Map<String, SQL> sqlMap = new HashMap<String, SQL>();
		
		if (globalVar == null) {
			globalVar = new HashMap<String, String>();
		}

		SQLMapper mapper = new SQLMapper();

		Unmarshaller bind = null;
		try {
			InputStreamReader stream = new ResourceReader().getFileStream(path + filename);
			bind = new XMLUnmarshaller(stream);

			bind.setRootClass(SQLMapper.class);
			mapper = (SQLMapper) bind.unmarshal();
		} catch (NoSuchPathException e) {
			throw new IOException(e);
		} catch (UnmarshalException e) {
			throw new IOException(e);
		}
		
		// 载入命名空间
		String namespace = mapper.getNamespaces();

		// 载入全局变量
		List<GlobalVar> globals = mapper.getGlobalVarList();
		for (GlobalVar global : globals) {
			globalVar.put(global.getKey(), global.getValue());
		}

		Set<String> keySet = globalVar.keySet();

		List<SQL> sqlList = mapper.getSqlList();
		for (SQL sql : sqlList) {
			String sqlStr = sql.getValue();
			for (String key : keySet) {
				// 全局变量替换
				String value = globalVar.get(key);
				key = ":" + key;
				if (sqlStr.contains(key)) {
					sqlStr = sqlStr.replaceAll(key, value);
				}
			}
			sql.setValue(sqlStr);
			
			String sqlName =sql.getName();
			if (StringUtils.isNoneBlank(namespace)) {
				sqlName = namespace.trim() + "." + sqlName;
			}
			
			sqlMap.put(sqlName, sql);
		}

		// 载入引入SQL
		List<Import> imports = mapper.getImportList();
		for (Import sqlImport : imports) {
			sqlMap.putAll(read(path, sqlImport.getFile(), globalVar));
		}
		
		return sqlMap;

	}
}
