package org.pinae.timon.io;

import java.util.ArrayList;
import java.util.List;

import org.pinae.nala.xb.annotation.Attribute;
import org.pinae.nala.xb.annotation.Element;
import org.pinae.nala.xb.annotation.ElementValue;
import org.pinae.nala.xb.annotation.Root;

/**
 * SQL配置文件映射对象
 * 
 * @author huiyugeng
 *
 */
@Root(name = "mapper")
public class SqlMapper {
	@Attribute(name="namespaces")
	private String namespaces;
	
	@Element(name = "env")
	private List<Env> envList = new ArrayList<Env>();
	
	@Element(name = "import")
	private List<Import> importList = new ArrayList<Import>();
	
	@Element(name = "script")
	private List<Script> scriptList = new ArrayList<Script>();

	@Element(name = "global")
	private List<GlobalVar> globalVarList = new ArrayList<GlobalVar>();

	@Element(name = "sql")
	private List<SqlObject> sqlList = new ArrayList<SqlObject>();
	
	public String getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(String namespace) {
		this.namespaces = namespace;
	}

	public List<Env> getEnvList() {
		return envList;
	}

	public void setEnvList(List<Env> envList) {
		this.envList = envList;
	}

	public List<SqlObject> getSqlList() {
		return sqlList;
	}

	public void addSql(SqlObject sql) {
		this.sqlList.add(sql);
	}

	public List<Import> getImportList() {
		return importList;
	}

	public void addImport(Import sqlImport) {
		this.importList.add(sqlImport);
	}

	public List<GlobalVar> getGlobalVarList() {
		return globalVarList;
	}

	public void addGlobalVar(GlobalVar global) {
		this.globalVarList.add(global);
	}

	public List<Script> getScriptList() {
		return scriptList;
	}

	public void addScript(Script script) {
		this.scriptList.add(script);
	}
	
	public class Env {
		@Attribute(name = "key")
		private String key; // 环境变量键
		
		@Attribute(name = "value")
		private String value; // 环境变量值

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
	
	public class Script {
		@Attribute(name = "name")
		private String name;
		
		@Attribute(name = "file")
		private String file;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getFile() {
			return file;
		}

		public void setFile(String file) {
			this.file = file;
		}
		
	}

	public class SqlObject {
		@Attribute(name = "name")
		private String name; // SQL名称
		
		@Attribute(name = "prepare")
		private boolean prepare; // 是否使用预编译SQL

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

		public boolean isPrepare() {
			return prepare;
		}

		public void setPrepare(boolean prepare) {
			this.prepare = prepare;
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
			
			@Attribute(name = "block")
			private String block; //替换位置

			@ElementValue
			private String value; // 条件值

			public String getWhen() {
				return when;
			}

			public void setWhen(String when) {
				this.when = when;
			}

			public String getBlock() {
				return block;
			}

			public void setBlock(String block) {
				this.block = block;
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
