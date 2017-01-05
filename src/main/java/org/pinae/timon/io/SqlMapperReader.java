package org.pinae.timon.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.pinae.nala.xb.exception.NoSuchPathException;
import org.pinae.nala.xb.exception.UnmarshalException;
import org.pinae.nala.xb.unmarshal.Unmarshaller;
import org.pinae.nala.xb.unmarshal.XmlUnmarshaller;
import org.pinae.nala.xb.util.ResourceReader;
import org.pinae.timon.io.SqlMapper.Env;
import org.pinae.timon.io.SqlMapper.GlobalVar;
import org.pinae.timon.io.SqlMapper.Import;
import org.pinae.timon.io.SqlMapper.Script;
import org.pinae.timon.io.SqlMapper.SqlObject;

import com.alibaba.fastjson.JSON;

/**
 * 从配置文件中读取SQL配置
 * 
 * @author huiyugeng
 *
 */
public class SqlMapperReader {

	private Map<String, SqlObject> sqlMap = new HashMap<String, SqlObject>();
	private Map<String, String> scriptMap = new HashMap<String, String>();
	private Map<String, String> envMap = new HashMap<String, String>();

	public SqlMapperReader(File file) throws IOException {
		if (file != null) {
			loadMapper(file.getParent(), file.getName(), null);
		}
	}

	public SqlMapperReader(String path, String filename) throws IOException {
		if (filename != null) {
			loadMapper(path, filename, null);
		}
	}

	/**
	 * 获取SQL列表
	 * 
	 * @return SQL列表 (SQL名称, SQL对象)
	 */
	public Map<String, SqlObject> getSQLMap() {
		return this.sqlMap;
	}

	/**
	 * 获取脚本文件列表
	 * 
	 * @return 脚本文件列表 (脚本名称, 脚本路径)
	 */
	public Map<String, String> getScriptMap() {
		return this.scriptMap;
	}

	/**
	 * 获取环境变量键值集合
	 * 
	 * @return 环境变量键值
	 */
	public Map<String, String> getEnvMap() {
		return this.envMap;
	}
	
	private void loadMapper(String path, String filename, Map<String, String> globalVar) throws IOException {
		try {
			
			if (filename.endsWith("xml")) {
				InputStreamReader stream = new ResourceReader().getFileStream(path + File.separator + filename);
				loadMapperFromXml(path, stream, globalVar);
			} else if (filename.endsWith("json")) {
				StringBuffer text = new ResourceReader().readFile(path + File.separator + filename);
				if (text != null) {
					loadMapperFromJson(path, text.toString(), null);
				} else {
					throw new IOException(filename + " is blank file");
				}
			}
		} catch (NoSuchPathException e) {
			throw new IOException(e);
		}
	}

	private void loadMapperFromXml(String path, InputStreamReader stream, Map<String, String> globalVar) throws IOException {

		SqlMapper mapper = new SqlMapper();

		Unmarshaller bind = null;
		try {
			bind = new XmlUnmarshaller(stream);

			bind.setRootClass(SqlMapper.class);
			mapper = (SqlMapper) bind.unmarshal();
			build(path, mapper, globalVar);
		} catch (UnmarshalException e) {
			throw new IOException(e);
		}
	}
	
	private void loadMapperFromJson(String path, String jsonText, Map<String, String> globalVar) throws IOException {
		SqlMapper mapper = new SqlMapper();
		try {
			mapper = JSON.parseObject(jsonText, SqlMapper.class);
			build(path, mapper, globalVar);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	private void build(String path, SqlMapper mapper, Map<String, String> globalVar) throws IOException {
		Map<String, String> subGlobalVar = new HashMap<String, String>();
		if (globalVar != null) {
			subGlobalVar.putAll(globalVar);
		}
		// 载入命名空间
		String namespace = mapper.getNamespaces();

		// 载入全局变量
		List<GlobalVar> globals = mapper.getGlobal();
		for (GlobalVar global : globals) {
			subGlobalVar.put(global.getKey(), global.getValue());
		}

		Set<String> keySet = subGlobalVar.keySet();

		List<SqlObject> sqlObjList = new ArrayList<SqlObject>();
		sqlObjList.addAll(mapper.getSql());
		sqlObjList.addAll(mapper.getProcedure());
		
		for (SqlObject sqlObj : sqlObjList) {
			String sqlName = sqlObj.getName();
			String query = sqlObj.getValue();

			if (StringUtils.isBlank(sqlName)) {
				continue;
			}

			if (query != null) {
				for (String key : keySet) {
					// 全局变量替换
					if (StringUtils.isNotBlank(key)) {
						String varName = ":" + key;
						if (query.contains(varName)) {
							String value = subGlobalVar.get(key);
							query = query.replaceAll(varName, value);
						}
					}
				}
				sqlObj.setValue(query);
			}

			// 使用命名空间构建SQL名称
			if (StringUtils.isNotBlank(namespace)) {
				sqlName = namespace.trim() + "." + sqlName;
			}

			sqlObj.setValue(query);
			this.sqlMap.put(sqlName, sqlObj);

		}

		// 读取SQL脚本文件列表
		List<Script> scriptList = mapper.getScript();
		for (Script script : scriptList) {
			String scriptName = script.getName();
			String scriptFile = script.getFile();

			if (StringUtils.isNotBlank(scriptName) && StringUtils.isNotBlank(scriptFile)) {
				this.scriptMap.put(scriptName, scriptFile);
			}
		}

		// 载入引入SQL文件
		List<Import> importFileList = mapper.getImports();
		for (Import importFile : importFileList) {
			loadMapper(path, importFile.getFile(), subGlobalVar);
		}

		// 载入环境变量
		List<Env> envList = mapper.getEnv();
		for (Env env : envList) {
			this.envMap.put(env.getKey(), env.getValue());
		}

	}

}
