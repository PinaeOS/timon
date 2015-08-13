package org.pinae.timon.helper;

import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import org.apache.log4j.Logger;
import org.pinae.timon.helper.parser.AlterParser;
import org.pinae.timon.helper.parser.CreateTableParser;
import org.pinae.timon.helper.parser.CreateViewParser;
import org.pinae.timon.helper.parser.DeleteParser;
import org.pinae.timon.helper.parser.DropParser;
import org.pinae.timon.helper.parser.InsertParser;
import org.pinae.timon.helper.parser.SelectParser;
import org.pinae.timon.helper.parser.UpdateParser;

/**
 * SQL语法解析器
 * 
 * @author Huiyugeng
 *
 */
public class SqlParser {
	
	private static Logger log = Logger.getLogger(SqlParser.class);
	
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
			log.debug(String.format("SQL Parse Exception: exception=%s, sql=%s", e.getMessage(), sql));
		} catch (Error e) {
			log.debug(String.format("SQL Parse Exception: exception=%s, sql=%s", e.getMessage(), sql));
		}
		
		return tableSet;
	}
	
	/**
	 * 获取SQL语句中所涉及的数据表和字段
	 * 
	 * @param sql SQL语句
	 * 
	 * @return SQL语句中<数据表, 字段列表>
	 */
	public Map<String, List<String>> getColumn(String sql) {
		Map<String, List<String>> table = new HashMap<String, List<String>>();
		Set<String> tableSet = getTable(sql);
		for (String tableName : tableSet) {
			table.put(tableName, null);
		}
		return table;
	}

}
