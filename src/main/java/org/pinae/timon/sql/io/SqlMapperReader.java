package org.pinae.timon.sql.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.pinae.nala.xb.exception.UnmarshalException;
import org.pinae.nala.xb.unmarshal.Unmarshaller;
import org.pinae.nala.xb.unmarshal.XmlUnmarshaller;
import org.pinae.timon.sql.SqlMapper;
import org.pinae.timon.sql.SqlMapper.Env;
import org.pinae.timon.sql.SqlMapper.GlobalVar;
import org.pinae.timon.sql.SqlMapper.Import;
import org.pinae.timon.sql.SqlMapper.Script;
import org.pinae.timon.sql.SqlMapper.SqlObject;
import org.pinae.timon.util.FileUtils;

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
	private Map<String, String> varMap = new HashMap<String, String>();

	public SqlMapperReader(File file) throws IOException {
		if (file != null) {
			loadMapper(file.getName(), new FileInputStream(file), null);
		}
	}
	
	public SqlMapperReader(String filename, InputStream inputStream) throws IOException {
		if (filename != null && inputStream != null) {
			loadMapper(filename, inputStream, null);
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
	public Map<String, String> getVarMap() {
		return this.varMap;
	}
	
	private void loadMapper(String filename, InputStream sqlMapperInputSteam, Map<String, String> globalVar) throws IOException {
		if (filename.endsWith("xml")) {
			InputStreamReader sqlMapperStreamReader = new InputStreamReader(sqlMapperInputSteam);
			loadMapperFromXml(sqlMapperStreamReader, globalVar);
		} else if (filename.endsWith("json")) {
			StringBuffer sqlMapperContent = FileUtils.read(sqlMapperInputSteam);
			if (sqlMapperContent != null) {
				loadMapperFromJson(sqlMapperContent.toString(), null);
			} else {
				throw new IOException(filename + " is blank file");
			}
		}
	}

	private void loadMapperFromXml(InputStreamReader sqlMapperInputSteam, Map<String, String> globalVar) throws IOException {

		SqlMapper sqlMapper = new SqlMapper();
		
		try {
			Unmarshaller bind = new XmlUnmarshaller(sqlMapperInputSteam);

			bind.setRootClass(SqlMapper.class);
			sqlMapper = (SqlMapper) bind.unmarshal();
			build(sqlMapper, globalVar);
		} catch (UnmarshalException e) {
			throw new IOException(e);
		}
	}
	
	private void loadMapperFromJson(String sqlMapperContent, Map<String, String> globalVar) throws IOException {
		SqlMapper sqlMapper = new SqlMapper();
		try {
			sqlMapper = JSON.parseObject(sqlMapperContent, SqlMapper.class);
			build(sqlMapper, globalVar);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	private void build(SqlMapper sqlMapper, Map<String, String> globalVarMap) throws IOException {
		Map<String, String> subGlobalVar = new HashMap<String, String>();
		if (globalVarMap != null) {
			subGlobalVar.putAll(globalVarMap);
		}
		// 载入命名空间
		String namespace = sqlMapper.getNamespaces();

		// 载入全局变量
		List<GlobalVar> globalVarList = sqlMapper.getGlobal();
		for (GlobalVar globalVar : globalVarList) {
			subGlobalVar.put(globalVar.getKey(), globalVar.getValue());
		}

		Set<String> varKeySet = subGlobalVar.keySet();

		List<SqlObject> sqlObjList = new ArrayList<SqlObject>();
		sqlObjList.addAll(sqlMapper.getSql());
		sqlObjList.addAll(sqlMapper.getProcedure());
		
		for (SqlObject sqlObj : sqlObjList) {
			String sqlName = sqlObj.getName();
			String sqlQuery = sqlObj.getValue();

			if (StringUtils.isBlank(sqlName)) {
				continue;
			}

			if (sqlQuery != null) {
				for (String varKey : varKeySet) {
					// 全局变量替换
					if (StringUtils.isNotBlank(varKey)) {
						String varName = ":" + varKey;
						if (sqlQuery.contains(varName)) {
							String varValue = subGlobalVar.get(varKey);
							sqlQuery = sqlQuery.replaceAll(varName, varValue);
						}
					}
				}
				sqlObj.setValue(sqlQuery);
			}

			// 使用命名空间构建SQL名称
			if (StringUtils.isNotBlank(namespace)) {
				sqlName = namespace.trim() + "." + sqlName;
			}

			sqlObj.setValue(sqlQuery);
			this.sqlMap.put(sqlName, sqlObj);

		}

		// 读取SQL脚本文件列表
		List<Script> scriptList = sqlMapper.getScript();
		for (Script script : scriptList) {
			String scriptName = script.getName();
			String scriptFile = script.getFile();

			if (StringUtils.isNotBlank(scriptName) && StringUtils.isNotBlank(scriptFile)) {
				this.scriptMap.put(scriptName, scriptFile);
			}
		}

		// 载入引入SQL文件
		List<Import> importFileList = sqlMapper.getImports();
		for (Import importFile : importFileList) {
			String importFilename = importFile.getFile();
			loadMapper(importFilename, FileUtils.getFileInputStream(importFilename), subGlobalVar);
		}

		// 载入环境变量
		List<Env> envList = sqlMapper.getEnv();
		for (Env env : envList) {
			this.varMap.put(env.getKey(), env.getValue());
		}

	}

}
