package org.pinae.timon.sql;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.pinae.timon.sql.SqlMapper.ProcedureObject;
import org.pinae.timon.sql.SqlMapper.ProcedureObject.Out;
import org.pinae.timon.sql.SqlMapper.SqlObject;
import org.pinae.timon.sql.SqlMapper.SqlObject.Choose;
import org.pinae.timon.sql.io.SqlMapperReader;
import org.pinae.timon.sql.io.SqlScriptReader;
import org.pinae.timon.util.FileUtils;

/**
 * 
 * 从SQL配置文件中载入SQL语句
 * 
 * @author huiyugeng
 * 
 */
public class SqlBuilder {

	private Map<String, SqlObject> sqlMap = new HashMap<String, SqlObject>();
	private Map<String, String> scriptMap = new HashMap<String, String>();
	private Map<String, String> envMap = new HashMap<String, String>();

	public SqlBuilder() {

	}

	public SqlBuilder(File file) throws IOException {
		this(new SqlMapperReader(file));
	}
	
	public SqlBuilder(String filename, InputStream inputStream) throws IOException {
		this(new SqlMapperReader(filename, inputStream));
	}

	private SqlBuilder(SqlMapperReader reader) {
		this.sqlMap = reader.getSQLMap();
		this.scriptMap = reader.getScriptMap();
		this.envMap = reader.getEnvMap();
	}

	public SqlBuilder(Map<String, SqlObject> sqlMap, Map<String, String> scriptMap, Map<String, String> envMap) {
		this.sqlMap = sqlMap;
		this.scriptMap = scriptMap;
		this.envMap = envMap;
	}

	/**
	 * 根据SQL描述名称获取SQL
	 * 
	 * @param name SQL描述名称
	 * 
	 * @return SQL语句
	 */
	public Sql getSQLByName(String name) {
		return getSQLByNameWithParameters(name, null);
	}

	/**
	 * 根据SQL描述名称和参数构建SQL语句
	 * 
	 * @param name SQL描述名称
	 * @param parameters 参数对象
	 * 
	 * @return 构建后的SQL语句
	 */

	public Sql getSQLByNameWithParameters(String name, Object parameters) {
		if (name == null) {
			return null;
		}

		Map<String, Object> parameterMap = buildParameters(parameters);

		SqlObject sqlObj = this.sqlMap.get(name);
		if (sqlObj != null) {

			if (sqlObj.isPrepare() == false || (this.envMap.containsKey("prepare") == false && "false".equalsIgnoreCase(this.envMap.get("prepare")))) {
				String sql = replaceSQL(sqlObj, parameterMap);
				return new Sql(sql, parameterMap);
			} else {
				String sql = replaceChoose(sqlObj, parameterMap);
				if (sqlObj instanceof ProcedureObject) {
					ProcedureObject procedureObj = (ProcedureObject)sqlObj;
					List<Out> outList = procedureObj.getOut();
					for (Out out : outList) {
						parameterMap.put(out.getName(), out);
					}
				}
				return getPreperSQLWithParameters(sql, parameterMap);
			}
		} else {
			return null;
		}
	}

	/*
	 * 构建预编译SQL所需要的SQL和变量
	 * 
	 * 将<key, value> 的值对转换为 PreperedStatement使用的<index, value>
	 */
	private Sql getPreperSQLWithParameters(String query, Map<String, Object> parameters) {
		// 检索query语句中所有:key的标记
		Pattern pattern = Pattern.compile(":\\w+");
		Matcher matcher = pattern.matcher(query);

		// 构建<key, index>值对
		int index = 1;
		Map<String, Integer> keyIndexs = new HashMap<String, Integer>();
		while (matcher.find()) {
			String word = matcher.group();
			if (word.startsWith(":")) {
				keyIndexs.put(word.substring(1), index++);
			}
		}

		// 构建<index, value>值对
		Map<Integer, Object> prepareValue = new HashMap<Integer, Object>();
		Set<String> paramKeySet = parameters.keySet();
		for (String paramKey : paramKeySet) {
			Integer preIndex = keyIndexs.get(paramKey);
			Object preValue = parameters.get(paramKey);

			prepareValue.put(preIndex, preValue);
		}

		// 替换所有的变量为"?"
		query = matcher.replaceAll("?");

		Sql sql = new Sql(query, parameters);
		sql.setPreperStatement(true);
		sql.setKeyIndexs(keyIndexs);
		sql.setPreperValues(prepareValue);

		return sql;
	}

	/*
	 * 构建SQL语句参数表
	 * 
	 * @param parameters 参数对象
	 * 
	 * @return 参数表<参数名称, 参数值>
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> buildParameters(Object parameters) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();

		if (parameters != null) {
			if (parameters.getClass().isArray()) {
				Object[] paramValues = (Object[]) parameters;
				for (int i = 0; i < paramValues.length; i++) {
					if (paramValues[i] == null) {
						paramValues[i] = "";
					}
					parameterMap.put(Integer.toString(i), paramValues[i]);
				}
			} else if (parameters instanceof Map) {
				parameterMap = (Map<String, Object>) parameters;
			} else {
				Class<? extends Object> paramClass = parameters.getClass();
				Field[] fields = paramClass.getDeclaredFields();
				for (Field field : fields) {
					field.setAccessible(true);

					String fieldName = field.getName();
					Object fieldValue = null;
					try {
						fieldValue = field.get(parameters);
					} catch (Exception e) {
						fieldValue = null;
					}
					if (fieldName != null && fieldValue != null) {
						parameterMap.put(fieldName, fieldValue);
					}
				}
			}
		}

		return parameterMap;
	}

	/*
	 * 替引用的SQL
	 * 
	 * @param sql 需要构建的SQL对象
	 * 
	 * @param subSQLs 引用的其他SQL语句
	 * 
	 * @return 构建后的SQL语句
	 */
	private String replaceSQL(SqlObject sqlObj, Map<String, Object> subSQLs) {
		String query = sqlObj.getValue();

		if (query != null) {
			String regexs = "[$][{](\\S*)[}]"; // 子句替换模式
			Pattern regex = Pattern.compile(regexs);
			Matcher regexMatcher = regex.matcher(query);
			while (regexMatcher.find()) {
				String subSQLName = regexMatcher.group(1);

				SqlObject subSQL = this.sqlMap.get(subSQLName);
				if (subSQL != null) {
					String subSQLContent = replaceSQL(subSQL, subSQLs);

					query = query.replace(regexMatcher.group(0), subSQLContent);
					sqlObj.setValue(query);
				}
			}

			query = replaceChoose(sqlObj, subSQLs);
			query = replaceVariables(query, subSQLs);
		}

		return query;
	}

	/*
	 * 替换SQL语句中的条件
	 * 
	 * @param sql 需要构建的SQL对象
	 * 
	 * @param parameters 参数表
	 * 
	 * @return 构建后的SQL语句
	 */
	private String replaceChoose(SqlObject sqlObj, Map<String, Object> parameters) {
		String query = null;
		if (sqlObj != null) {
			query = sqlObj.getValue();
			if (StringUtils.isNotEmpty(query)) {

				try {
					// 执行子句构建 子句格式为{statement}
					List<Choose> chooseList = sqlObj.getChoose();

					if (chooseList != null && parameters != null) {
						for (Choose choose : chooseList) {
							String condition = choose.getWhen();
							if (parameters.containsKey(condition)) {
								String statement = choose.getBlock();

								// 如果不存在statement参数，则使用when参数代替
								if (StringUtils.isEmpty(statement)) {
									statement = condition;
								}
								statement = "{" + statement + "}";

								if (StringUtils.contains(query, statement)) {
									query = query.replace(statement, choose.getValue());
								} else {
									query = query + " " + choose.getValue();
								}
							}
						}
					}

					// 清理没有被替换的子句
					String regexs = "[{](\\w*)[}]"; // 子句替换模式
					Pattern regex = Pattern.compile(regexs);
					Matcher regexMatcher = regex.matcher(query);
					while (regexMatcher.find()) {
						query = query.replace(regexMatcher.group(0), "");
					}

				} catch (Exception e) {

				}
			}
		}

		return query;
	}

	/*
	 * 替换SQL语句的变量
	 * 
	 * @param sql SQL语句
	 * 
	 * @param parameters 参数表
	 * 
	 * @return 构建后的SQL语句
	 */
	private String replaceVariables(String query, Map<String, Object> parameters) {
		if (StringUtils.isNotEmpty(query)) {
			try {
				if (parameters != null) {
					// 执行变量替换, 变量格式为:var
					Set<String> keySet = parameters.keySet();
					for (String key : keySet) {
						Object value = parameters.get(key);

						if (value instanceof String) {
							value = String.format("'%s'", value);
						} else if (value instanceof List) {
							StringBuffer valueBuffer = new StringBuffer();

							List<?> list = (List<?>) value;
							for (Object item : list) {
								if (item instanceof String) {
									valueBuffer.append(String.format("'%s'", item) + ",");
								} else {
									valueBuffer.append(item.toString() + ",");
								}
							}
							value = StringUtils.removeEnd(valueBuffer.toString(), ",");
						} else if (value == null) {
							continue;
						} else if (value instanceof Date) {
							value = String.format("'%s'", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) value));
						} else {
							value = value.toString();
						}

						key = ":" + key;
						query = StringUtils.replace(query, key, (String) value);
					}
				}

				// 多个空格替换为单个空格
				if (query != null) {
					query = query.replaceAll(" +", " ");
				}
			} catch (Exception e) {
				return null;
			}
		}

		return query;
	}
	
	/**
	 * 获得SQL脚本中SQL语句列表
	 * 
	 * @param scriptName SQL脚本名称
	 * 
	 * @return SQL语句列表
	 */
	public List<String> getScript(String scriptName) {
		String scriptFilename = this.scriptMap.get(scriptName);
		InputStream scriptFileStream = FileUtils.getFileInputStream(scriptFilename);
		
		List<String> sqlList = new SqlScriptReader().getSQLList(scriptFileStream);

		return sqlList;
	}

	/**
	 * 获得SQL脚本中SQL语句列表
	 * 
	 * @param scriptFileStream SQL脚本文件流
	 * 
	 * @return SQL语句列表
	 */
	public List<String> getScript(InputStream scriptFileStream) {
		List<String> sqlList = new SqlScriptReader().getSQLList(scriptFileStream);

		return sqlList;
	}

	/**
	 * 限制查询条数的SQL
	 * 
	 * @param query SQL语句
	 * @param offset SQL查询偏移记录数
	 * @param length SQL目标数量
	 * @param dbType 数据库类型 oralce ,mysql
	 * 
	 * @return 限制查询条数的SQL
	 */
	public static String getLimitSQL(String query, long offset, long length, String dbType) {
		if (query == null) {
			return null;
		}
		query = query.trim();
		if (StringUtils.startsWithIgnoreCase(query, "select")) {
			boolean isForUpdate = false;
			if (query.toLowerCase().endsWith(" for update")) {
				query = query.substring(0, query.length() - 11);
				isForUpdate = true;
			}

			StringBuffer pagingSelect = new StringBuffer(query.length() + 100);
			if (dbType.equalsIgnoreCase("oracle")) {
				if (offset > 0) {
					pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
				} else {
					pagingSelect.append("select * from ( ");
				}
				pagingSelect.append(query);
				if (offset > 0) {
					pagingSelect.append(String.format(" ) row_ where rownum <= %d) where rownum_ > %d", length + offset, offset));
				} else {
					pagingSelect.append(String.format(" ) where rownum <= %d", length));
				}
			} else if (dbType.equalsIgnoreCase("mysql")) {
				pagingSelect.append("select * from ( ");
				pagingSelect.append(query);
				pagingSelect.append(String.format(" ) t limit %d, %d", offset, length));
			}

			if (isForUpdate) {
				pagingSelect.append(" for update");
			}

			return pagingSelect.toString();
		}
		return query;
	}

	/**
	 * 获得查询SQL中可返回的结果数量的SQL语句
	 * 
	 * @param query 原SQL语句
	 * 
	 * @return SQL可返回的结果数量SQL语句
	 */
	public static String getCountSQL(String query) {
		if (query == null) {
			return null;
		}
		query = query.trim();
		if (StringUtils.startsWithIgnoreCase(query, "select")) {
			StringBuffer pagingSelect = new StringBuffer(query.length() + 30);
			pagingSelect.append("select count(*) from ( ");
			pagingSelect.append(query);
			pagingSelect.append(" ) t");
			return pagingSelect.toString();
		}
		return null;
	}

	/**
	 * 构建SQL对象
	 * 
	 * @param query 原SQL语句
	 * @param parameters 参数表
	 * 
	 * @return SQL对象
	 */
	public static Sql getSql(String query, Map<String, Object> parameters) {
		SqlBuilder builder = new SqlBuilder();
		String sql = builder.replaceVariables(query, parameters);
		return new Sql(sql, parameters);
	}

	/**
	 * 构建SQL对象
	 * 
	 * @param query 原SQL语句
	 * @param parameters 参数表
	 * 
	 * @return SQL对象
	 */
	public static Sql getPreperSQL(String query, Map<String, Object> parameters) {
		SqlBuilder builder = new SqlBuilder();
		return builder.getPreperSQLWithParameters(query, parameters);
	}
}
