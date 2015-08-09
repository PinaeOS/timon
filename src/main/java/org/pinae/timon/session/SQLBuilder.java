package org.pinae.timon.session;

import java.io.File;
import java.io.IOException;
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
import org.pinae.timon.io.SQLMapper.SQL;
import org.pinae.timon.io.SQLMapper.SQL.Choose;
import org.pinae.timon.io.SQLScriptReader;
import org.pinae.timon.io.XMLMapperReader;
import org.pinae.timon.util.ClassLoaderUtils;

/**
 * 
 * 从XML配置中获得SQL语句
 * 
 * 
 * @author huiyugeng
 * 
 */
public class SQLBuilder {

	private Map<String, SQL> sqlMap = new HashMap<String, SQL>();
	private Map<String, String> scriptMap = new HashMap<String, String>();
	
	private String path;
	
	public SQLBuilder() throws IOException {
		this.path = ClassLoaderUtils.getResourcePath("");
		
		try {
			XMLMapperReader reader = new XMLMapperReader(this.path, "sql.xml");
			
			this.sqlMap = reader.getSQLMap();
			this.scriptMap = reader.getScriptMap();
		} catch (IOException e) {
			throw e;
		}
	}
	
	public SQLBuilder(String path, String filename) throws IOException {
		
		this.path = path;
		
		try {
			XMLMapperReader reader = new XMLMapperReader(this.path, filename);
			
			this.sqlMap = reader.getSQLMap();
			this.scriptMap = reader.getScriptMap();
		} catch (IOException e) {
			throw e;
		}	
	}
	
	public SQLBuilder(Map<String, SQL> sqlMap, Map<String, String> scriptMap) {
		this.sqlMap = sqlMap;
		this.scriptMap = scriptMap;
	}

	/**
	 * 根据SQL描述名称获取SQL
	 * 
	 * @param name SQL描述名称
	 * 
	 * @return SQL语句
	 */
	public String getSQLByName(String name) {
		return getSQLByName(name, null);
	}

	/**
	 * 根据SQL描述名称和参数获取SQL
	 * 
	 * @param name SQL描述名称
	 * @param statments 需要替换的SQL语句
	 * 
	 * @return SQL语句
	 */
	public String getSQLByName(String name, Map<String, Object> statments) {
		SQL sql = this.sqlMap.get(name);
		if (sql != null) {
			return replaceStatement(sql, statments);
		} else {
			return null;
		}
	}

	/**
	 * 根据SQL描述名称和参数构建SQL语句
	 * 
	 * @param name SQL描述名称
	 * @param parameters 参数对象
	 * 
	 * @return 构建后的SQL语句
	 */

	public String getSQLByNameWithParameters(String name, Object parameters) {
		if (name == null) {
			return null;
		}
		
		Map<String, Object> parameterMap = buildParameters(parameters);
		
		SQL sql = this.sqlMap.get(name);
		if (sql != null) {
			return replaceSQL(sql, parameterMap);
		} else {
			return null;
		}
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
	 * @param subSQLs 引用的其他SQL语句
	 * 
	 * @return 构建后的SQL语句
	 */
	private String replaceSQL(SQL sql, Map<String, Object> subSQLs) {
		String sqlContent = sql.getValue();
		
		if (sqlContent != null) {
			String regexs = "[$][{](\\w*)[}]"; //子句替换模式
			Pattern regex = Pattern.compile(regexs);
			Matcher regexMatcher = regex.matcher(sqlContent);
			while (regexMatcher.find()) {
				String subSQLName = regexMatcher.group(1);
				
				SQL subSQL = this.sqlMap.get(subSQLName);
				if (subSQL != null) {
					String subSQLContent = replaceSQL(subSQL, subSQLs);
					
					sqlContent = sqlContent.replace(regexMatcher.group(0), subSQLContent);
					sql.setValue(sqlContent);
				}
			}
			
			sqlContent = replaceStatement(sql, subSQLs);
			sqlContent = replaceVariables(sqlContent, subSQLs);
		}
		
		return sqlContent;
	}

	/*
	 * 替换SQL语句中的条件
	 * 
	 * @param sql 需要构建的SQL对象
	 * @param parameters 参数表
	 * 
	 * @return 构建后的SQL语句
	 */
	private String replaceStatement(SQL sql, Map<String, Object> parameters) {
		String sqlContent = null;
		if (sql != null) {
			sqlContent = sql.getValue();
			if (StringUtils.isNotEmpty(sqlContent)) {

				try {
					// 执行子句构建 子句格式为{statement}
					List<Choose> chooseList = sql.getChooseList();

					if (chooseList != null && parameters != null) {
						for (Choose choose : chooseList) {
							String condition = choose.getWhen();
							if (parameters.containsKey(condition)) {
								String statement = choose.getStatement();

								// 如果不存在statement参数，则使用when参数代替
								if (StringUtils.isNotEmpty(statement)) {
									statement = condition;
								}
								statement = "{" + statement + "}";

								if (StringUtils.contains(sqlContent, statement)) {
									sqlContent = sqlContent.replace(statement, choose.getValue());
								} else {
									sqlContent = sqlContent + " " + choose.getValue();
								}
							}
						}
					}
					
					//清理没有被替换的子句
					String regexs = "[{](\\w*)[}]"; //子句替换模式
					Pattern regex = Pattern.compile(regexs);
					Matcher regexMatcher = regex.matcher(sqlContent);
					while (regexMatcher.find()) {
						sqlContent = sqlContent.replace(regexMatcher.group(0), "");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return sqlContent;
	}

	/*
	 * 替换SQL语句的变量
	 * 
	 * @param sql SQL语句
	 * @param parameters 参数表
	 * 
	 * @return 构建后的SQL语句
	 */
	private String replaceVariables(String sql, Map<String, Object> parameters) {
		if (StringUtils.isNotEmpty(sql)) {
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
							value = String.format("'%s'",
									new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) value));
						} else {
							value = value.toString();
						}

						key = ":" + key;
						sql = StringUtils.replace(sql, key, (String) value);
					}
				}

				// 多个空格替换为单个空格
				if (sql != null) {
					sql = sql.replaceAll(" +", " ");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return sql;
	}

	/**
	 * 获得SQL脚本中SQL语句列表
	 * 
	 * @param name SQL脚本名称
	 * 
	 * @return SQL语句列表
	 */
	public List<String> getScript(String name) {
		return getScript(name, "UTF8");
	}
	
	/**
	 * 获得SQL脚本中SQL语句列表
	 * 
	 * @param name SQL脚本名称
	 * @param encoding SQL脚本编码
	 * 
	 * @return SQL语句列表
	 */
	public List<String> getScript(String name, String encoding) {
		String filename = this.scriptMap.get(name);
		List<String> sqlList = new SQLScriptReader().getSQLList(this.path + File.separator + filename, encoding);
		
		return sqlList;
	}
	
	/**
	 * 限制查询条数的SQL
	 * 
	 * @param sql SQL语句
	 * @param offset SQL查询偏移记录数
	 * @param length SQL目标数量
	 * @param dbType 数据库类型 oralce ,mysql
	 * 
	 * @return 限制查询条数的SQL
	 */
	public static String getLimitSQL(String sql, long offset, long length, String dbType) {
		sql = sql.trim();
		if (StringUtils.startsWithIgnoreCase(sql, "select")) {
			boolean isForUpdate = false;
			if (sql.toLowerCase().endsWith(" for update")) {
				sql = sql.substring(0, sql.length() - 11);
				isForUpdate = true;
			}

			StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
			if (dbType.equalsIgnoreCase("oracle")) {
				if (offset > 0) {
					pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
				} else {
					pagingSelect.append("select * from ( ");
				}
				pagingSelect.append(sql);
				if (offset > 0) {
					pagingSelect.append(String.format(" ) row_ where rownum <= %d) where rownum_ > %d",
							length + offset, offset));
				} else {
					pagingSelect.append(String.format(" ) where rownum <= %d", length));
				}
			} else if (dbType.equalsIgnoreCase("mysql")) {
				pagingSelect.append("select * from ( ");
				pagingSelect.append(sql);
				pagingSelect.append(String.format(" ) t limit %d, %d", offset, length));
			}

			if (isForUpdate) {
				pagingSelect.append(" for update");
			}

			return pagingSelect.toString();
		}
		return sql;
	}

	/**
	 * 获得查询SQL中可返回的结果数量的SQL语句
	 * 
	 * @param sql 原SQL语句
	 * 
	 * @return SQL可返回的结果数量SQL语句
	 */
	public static String getCountSQL(String sql) {
		sql = sql.trim();
		if (StringUtils.startsWithIgnoreCase(sql, "select")) {
			StringBuffer pagingSelect = new StringBuffer(sql.length() + 30);
			pagingSelect.append("select count(*) from ( ");
			pagingSelect.append(sql);
			pagingSelect.append(" ) t");
			return pagingSelect.toString();
		}
		return null;
	}

}
