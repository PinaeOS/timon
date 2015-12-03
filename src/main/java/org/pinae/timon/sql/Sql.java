package org.pinae.timon.sql;

import java.util.HashMap;
import java.util.Map;

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
	
	/* 传入的参数集 */
	private Map<String, Object> parameters;
	
	/* 是否使用预编译模式 */
	private boolean prepareStatement = false;
	
	/* 预编译模式的值 */
	private Map<Integer, Object> prepareValues = new HashMap<Integer, Object>();
	
	public Sql() {
		
	}

	public Sql(String sql) {
		setSql(sql);
	}
	
	public Sql(String sql, Map<String, Object> parameters) {
		setSql(sql);
		this.parameters = parameters;
	}
	
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		if (StringUtils.isNotEmpty(sql)) {
			this.sql = sql.trim();
			this.comment = parseComment(sql);
			this.digest = MessageDigestUtils.MD5(sql);
			this.select = this.sql.toLowerCase().startsWith("select");
		}
	}
	
	public Map<String, String> getComment() {
		return comment;
	}

	public void setComment(Map<String, String> comment) {
		this.comment = comment;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	
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

	public Map<Integer, Object> getPreperValues() {
		return prepareValues;
	}

	public void setPreperValues(Map<Integer, Object> prepareValues) {
		this.prepareValues = prepareValues;
	}

	public String getDigest() {
		return digest;
	}
	
	public boolean validate() {
		return this.sql != null;
	}
	
	public boolean isSelect() {
		return select;
	}
	
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
