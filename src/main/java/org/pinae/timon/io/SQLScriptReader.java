package org.pinae.timon.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 从SQL脚本中读取SQL语句
 * 
 * @author Huiyugeng
 *
 */
public class SQLScriptReader {
	public List<String> getSQLList(String filename, String encoding) {
		
		List<String> content = getContent(filename, encoding);
		
		List<String> sqlList = new ArrayList<String>();
		
		StringBuffer sqlBuffer = new StringBuffer();
		for (String line : content) {
			
			if (StringUtils.contains(line, ";")) {
				for (Character word : line.toCharArray()) {
					if (word == ';') {
						String sql = sqlBuffer.toString().trim();
						
						sql = sql.replaceAll("\t", " "); //替换回车符为一个空格
						sql = sql.replaceAll(" +", " "); //替换多个空格为一个空格
						
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
		
		return sqlList;
	}
	
	public List<String> getContent(String filename, String encoding) {
		
		List<String> content = new ArrayList<String>();
		
		if (StringUtils.isEmpty(encoding)) {
			encoding = "UTF8";
		}
		
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		try {
			fis = new FileInputStream(filename);
			isr = new InputStreamReader(fis, encoding);
			br = new BufferedReader(isr);

			boolean commentFlag = false;
			
			String line = null;
			while ((line = br.readLine()) != null) {
				
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
					if(StringUtils.isNotBlank(line)) {
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
			
		} catch (IOException e) {

		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (isr != null) {
					isr.close();
				}
				if (fis != null) {
					fis.close();
				}
			}catch (IOException e) {

			}
		}
		
		return content;
	}
}
