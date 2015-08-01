package org.pinae.timon.io;

import java.util.ArrayList;
import java.util.List;

import org.pinae.nala.xb.annotation.Attribute;
import org.pinae.nala.xb.annotation.Element;
import org.pinae.nala.xb.annotation.ElementValue;
import org.pinae.nala.xb.annotation.Root;


@Root(name = "mapper")
public class SQLMapper {
	@Attribute(name="namespaces")
	private String namespaces;
	
	@Element(name = "import")
	private List<Import> importList = new ArrayList<Import>();

	@Element(name = "global")
	private List<GlobalVar> globalVarList = new ArrayList<GlobalVar>();

	@Element(name = "sql")
	private List<SQL> sqlList = new ArrayList<SQL>();
	
	public String getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(String namespace) {
		this.namespaces = namespace;
	}

	public List<SQL> getSqlList() {
		return sqlList;
	}

	public void setSqlList(SQL sql) {
		this.sqlList.add(sql);
	}

	public List<Import> getImportList() {
		return importList;
	}

	public void setImportList(Import sqlImport) {
		this.importList.add(sqlImport);
	}

	public List<GlobalVar> getGlobalVarList() {
		return globalVarList;
	}

	public void setGlobalVarList(GlobalVar global) {
		this.globalVarList.add(global);
	}

	public class Import {
		@Attribute(name = "file")
		private String file; // 引入文件名称

		public String getFile() {
			return file;
		}

		public void setFile(String file) {
			this.file = file;
		}

	}

	public class GlobalVar {
		@Attribute(name = "key")
		private String key; // 全局变量键名称

		@Attribute(name = "value")
		private String value; // 全局变量值名称

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

	public class SQL {
		@Attribute(name = "name")
		private String name; // SQL名称

		@Element(name = "choose")
		private List<Choose> chooseList = new ArrayList<Choose>(); // 选择条件

		@ElementValue
		private String value; // SQL语句

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<Choose> getChooseList() {
			return chooseList;
		}

		public void setChooseList(Choose choose) {
			this.chooseList.add(choose);
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public class Choose {
			@Attribute(name = "when")
			private String when; // 选择条件
			
			@Attribute(name = "statement")
			private String statement; //替换子句

			@ElementValue
			private String value; // 条件值ß

			public String getWhen() {
				return when;
			}

			public void setWhen(String when) {
				this.when = when;
			}

			public String getStatement() {
				return statement;
			}

			public void setStatement(String statement) {
				this.statement = statement;
			}

			public String getValue() {
				return value;
			}

			public void setValue(String value) {
				this.value = value;
			}

		}
	}

}
