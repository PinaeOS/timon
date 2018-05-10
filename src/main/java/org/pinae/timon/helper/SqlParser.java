package org.pinae.timon.helper;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.pinae.timon.helper.parser.AlterParser;
import org.pinae.timon.helper.parser.CreateTableParser;
import org.pinae.timon.helper.parser.CreateViewParser;
import org.pinae.timon.helper.parser.DeleteParser;
import org.pinae.timon.helper.parser.DropParser;
import org.pinae.timon.helper.parser.InsertParser;
import org.pinae.timon.helper.parser.SelectParser;
import org.pinae.timon.helper.parser.UpdateParser;

import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

/**
 * SQL语法解析器
 * 
 * @author Huiyugeng
 *
 */
public class SqlParser {
	
	private static Logger logger = Logger.getLogger(SqlParser.class);
	
	private CCJSqlParserManager parserManager = new CCJSqlParserManager();
	
	/**
	 * 获取SQL语句中所涉及的数据表
	 * 
	 * @param sql SQL语句
	 * 
	 * @return SQL语句中的数据表
	 */
	public Set<String> getTable(String sql) {
		Set<String> tableSet = new HashSet<String>();
		
		try {
			Statement statement = parserManager.parse(new StringReader(sql));
			
			if (statement instanceof Select) {
				tableSet = new SelectParser().parse((Select)statement);
			} else if (statement instanceof Delete) {
				tableSet = new DeleteParser().parse((Delete)statement);
			} else if (statement instanceof Insert) {
				tableSet = new InsertParser().parse((Insert)statement);
			} else if (statement instanceof Update) {
				tableSet = new UpdateParser().parse((Update)statement);
			} else if (statement instanceof CreateTable) {
				tableSet = new CreateTableParser().parse((CreateTable)statement);
			} else if (statement instanceof CreateView) {
				tableSet = new CreateViewParser().parse((CreateView)statement);
			} else if (statement instanceof Alter) {
				tableSet = new AlterParser().parse((Alter)statement);
			} else if (statement instanceof Drop) {
				tableSet = new DropParser().parse((Drop)statement);
			}
		} catch (Exception e) {
			logger.debug(String.format("SQL Parse Exception: exception=%s, sql=%s", e.getMessage(), sql));
		} catch (Error e) {
			logger.debug(String.format("SQL Parse Exception: exception=%s, sql=%s", e.getMessage(), sql));
		}
		
		Set<String> resultSet = new HashSet<String>();
		for (String tableName : tableSet) {
			if (StringUtils.isNotEmpty(tableName)) {
				if (tableName.startsWith("`")) {
					tableName = tableName.substring(1);
				}
				if (tableName.endsWith("`")) {
					tableName = tableName.substring(0, tableName.length() - 1);
				}
				resultSet.add(tableName.toUpperCase());
			}
		}
		
		return resultSet;
	}

}
