package org.pinae.timon.sql;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.pinae.timon.util.MessageDigestUtils;

/**
 * SQL语句对象
 * 
 * @author Huiyugeng
 *
 */
public class Sql {
	
	private static Logger logger = Logger.getLogger(Sql.class);
	
	/* Sql语句 */
	private String sql = null;
	
	/* Sql语句注释 */
	private Map<String, String> comment = new HashMap<String, String>();
	
	/* Sql语句摘要 */
	private String digest = null;
	
	/* 是否Select语句 */
	private boolean select = false;
	
	/* 是否存储过程 */
	private boolean procedure = false;
	
	/* 传入的参数集 */
	private Map<String, Object> parameters;
	
	/* 是否使用预编译模式 */
	private boolean prepareStatement = false;
	
	/* 预编译模式中变量名与变量位置关系  */
	private Map<String, Integer> keyIndexs = new HashMap<String, Integer>();
	
	/* 预编译模式的值 */
	private Map<Integer, Object> prepareValues = new HashMap<Integer, Object>();
	
	/**
	 * 构造函数
	 */
	public Sql() {
		
	}

	/**
	 * 构造函数
	 * 
	 * @param sql Sql语句
	 */
	public Sql(String sql) {
		setSql(sql);
	}
	
	/**
	 * 构造函数
	 * 
	 * @param sql Sql语句
	 * @param parameters Sql中使用的参数
	 */
	public Sql(String sql, Map<String, Object> parameters) {
		setSql(sql);
		this.parameters = parameters;
	}
	
	/**
	 * 获取Sql语句
	 * 
	 * @return Sql语句
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * 设置Sql语句
	 * 
	 * @param sql Sql语句
	 */
	public void setSql(String sql) {
		if (StringUtils.isNotEmpty(sql)) {
			this.sql = sql.trim();
			this.comment = parseComment(sql);
			this.digest = MessageDigestUtils.MD5(sql);
			
			String sqlStatement = this.sql.toLowerCase();
			if (sqlStatement != null) {
				this.select = sqlStatement.startsWith("select");
				this.procedure = sqlStatement.startsWith("call");
			}
		}
	}
	
	/**
	 * 获取Sql注释信息
	 * 
	 * Sql注释信息主要用于进行Sql功能标记, 例如缓存标记
	 * 
	 * @return 注释信息键值
	 */
	public Map<String, String> getComment() {
		return comment;
	}

	/**
	 * 设置Sql注释信息
	 * 
	 * @param comment 注释信息键值
	 */
	public void setComment(Map<String, String> comment) {
		this.comment = comment;
	}

	/**
	 * 获取Sql参数
	 * 
	 * Sql参数用于替换Sql语句中的变量
	 * 
	 * @return Sql参数键值
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}

	/**
	 * 设置Sql参数
	 * 
	 * @param parameters Sql参数键值
	 */
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * 是否使用PreperStatment
	 * 
	 * @return 是否使用PreperStatment
	 */
	public boolean isPreperStatement() {
		return prepareStatement;
	}

	public void setPreperStatement(boolean prepareStatement) {
		// 当启用PreperStatement时不进行缓存处理
		if (prepareStatement) {
			comment.put("CACHE", "FALSE");
		}
		this.prepareStatement = prepareStatement;
	}

	/**
	 * 获取Sql中变量名称与变量位置的对应关系
	 * 
	 * 仅对于PreperedStatment有效
	 * 
	 * @return Sql中变量名称与变量位置键值
	 */
	public Map<String, Integer> getKeyIndexs() {
		return keyIndexs;
	}

	
	/**
	 * 设置Sql中变量名称与变量位置的对应关系
	 * 
	 * 仅对于PreperedStatment有效
	 * 
	 * @param keyIndexs Sql中变量名称与变量位置键值
	 */
	public void setKeyIndexs(Map<String, Integer> keyIndexs) {
		this.keyIndexs = keyIndexs;
	}
	
	/**
	 * 根据Sql变量位置获取变量名称
	 * 
	 * 仅对于PreperedStatment有效
	 * 
	 * @param index 变量位置
	 * @return 变量名称
	 */
	public String getKeyByIndex(int index) {
		Set<String> keySet = keyIndexs.keySet();
		for (String key : keySet) {
			int idx = keyIndexs.get(key);
			if (index == idx) {
				return key;
			}
		}
		return null;
	}

	/**
	 * 获取预编译Sql的变量位置与变量值
	 * 
	 * 仅对于PreperedStatment有效
	 * 
	 * @return 预编译Sql的变量位置与变量值
	 */
	public Map<Integer, Object> getPreperValues() {
		return prepareValues;
	}

	/**
	 * 设置预编译Sql的变量位置与变量值
	 * 
	 * 仅对于PreperedStatment有效
	 * 
	 * @param prepareValues 预编译Sql的变量位置与变量值
	 */
	public void setPreperValues(Map<Integer, Object> prepareValues) {
		this.prepareValues = prepareValues;
	}

	/**
	 * 获取Sql语句的摘要值
	 * 
	 * @return Sql语句摘要值
	 */
	public String getDigest() {
		return digest;
	}
	
	/**
	 * Sql语句验证
	 * 
	 * @return 是否验证通过
	 */
	public boolean validate() {
		boolean validate = this.sql != null;
		return validate;
	}
	
	/**
	 * Sql对象中的Sql语句是否Select语句
	 * 
	 * @return 是否Select语句
	 */
	public boolean isSelect() {
		return select;
	}
	
	
	/**
	 * Sql对象中的Sql语句是否存储过程
	 * 
	 * @return 是否存储过程
	 */
	public boolean isProcedure() {
		return procedure;
	}

	/*
	 * 解析Sql语句中的注释信息
	 */
	private Map<String, String> parseComment(String sql) {
		Map<String, String> commentMap = new HashMap<String, String>();
		try {
		while (StringUtils.contains(sql, "/*") && StringUtils.contains(sql, "*/")) {
			String comment = StringUtils.substringBetween(sql, "/*", "*/");
			comment = comment.toUpperCase().trim();

			if (StringUtils.isNotEmpty(comment)) {
				if (comment.contains("NO_CACHE")) {
					commentMap.put("CACHE", "FALSE");
				}
				String commentItems[] = comment.split(",");
				for (String commentItem : commentItems) {
					if (commentItem != null && commentItem.contains("=")) {
						String value[] = commentItem.trim().split("=");
						if (value != null && value.length == 2) {
							commentMap.put(value[0].trim(), value[1].trim());
						}
					}
				}
			}

			sql = StringUtils.substringBefore(sql, "/*") + StringUtils.substringAfter(sql, "*/");
		}
		} catch (Exception e) {
			logger.error("Parse sql comment fail:" + sql);
		}
		return commentMap;
	}
	
	public String toString() {
		return sql;
	}
}
