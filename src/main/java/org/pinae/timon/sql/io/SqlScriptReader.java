package org.pinae.timon.sql.io;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pinae.timon.util.FileUtils;

/**
 * 从SQL脚本中读取SQL语句
 * 
 * @author Huiyugeng
 *
 */
public class SqlScriptReader {

	public List<String> getSQLList(InputStream scriptFileStream) {

		List<String> sqlList = new ArrayList<String>();

		if (scriptFileStream != null) {
			List<String> content = readFileWithoutComment(scriptFileStream);

			StringBuffer sqlBuffer = new StringBuffer();
			for (String line : content) {

				if (StringUtils.contains(line, ";")) {
					for (Character word : line.toCharArray()) {
						if (word == ';') {
							String sql = sqlBuffer.toString().trim();

							sql = sql.replaceAll("\t", " "); // 替换回车符为一个空格
							sql = sql.replaceAll(" +", " "); // 替换多个空格为一个空格

							sqlList.add(sql);
							sqlBuffer = new StringBuffer();
						} else {
							sqlBuffer.append(word);
						}
					}
				} else {
					sqlBuffer.append(String.format(" %s ", line));
				}
			}

		}
		return sqlList;
	}

	public List<String> readFileWithoutComment(InputStream scriptFileStream) {

		List<String> content = new ArrayList<String>();

		List<String> fileContent = FileUtils.readList(scriptFileStream);

		boolean commentFlag = false;

		for (String line : fileContent) {

			/*
			 * 处理单行中的注释信息
			 */
			while (StringUtils.contains(line, "/*") && StringUtils.contains(line, "*/")) {
				line = StringUtils.substringBefore(line, "/*") + StringUtils.substringAfter(line, "*/");
			}
			if (StringUtils.contains(line, "--")) {
				line = StringUtils.substringBefore(line, "--");
			}

			/*
			 * 处理多行注释信息
			 */
			if (StringUtils.contains(line, "/*") && !StringUtils.contains(line, "*/")) {
				line = StringUtils.substringBefore(line, "/*");
				if (StringUtils.isNotBlank(line)) {
					content.add(line);
				}
				commentFlag = true;
			}

			if (commentFlag == true && StringUtils.contains(line, "*/")) {
				line = StringUtils.substringAfter(line, "*/");
				commentFlag = false;
			}

			if (commentFlag == true) {
				continue;
			}

			if (StringUtils.isNotBlank(line)) {
				content.add(line);
			}
		}

		return content;
	}
}
