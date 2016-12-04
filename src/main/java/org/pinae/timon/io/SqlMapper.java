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
	private List<Env> env = new ArrayList<Env>();
	
	@Element(name = "imports")
	private List<Import> imports = new ArrayList<Import>();
	
	@Element(name = "script")
	private List<Script> script = new ArrayList<Script>();

	@Element(name = "global")
	private List<GlobalVar> global = new ArrayList<GlobalVar>();

	@Element(name = "sql")
	private List<SqlObject> sql = new ArrayList<SqlObject>();
	
	@Element(name = "procedure")
	private List<ProcedureObject> procedure = new ArrayList<ProcedureObject>();
	
	public String getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(String namespace) {
		this.namespaces = namespace;
	}

	public List<Env> getEnv() {
		return env;
	}

	public void setEnv(List<Env> envList) {
		this.env = envList;
	}

	public List<SqlObject> getSql() {
		return sql;
	}

	public void addSql(SqlObject sql) {
		this.sql.add(sql);
	}
	

	public List<ProcedureObject> getProcedure() {
		return procedure;
	}

	public void addProcedure(ProcedureObject procedure) {
		this.procedure.add(procedure);
	}

	public List<Import> getImports() {
		return imports;
	}

	public void addImport(Import sqlImport) {
		this.imports.add(sqlImport);
	}

	public List<GlobalVar> getGlobal() {
		return global;
	}

	public void addGlobalVar(GlobalVar global) {
		this.global.add(global);
	}

	public List<Script> getScript() {
		return script;
	}

	public void addScript(Script script) {
		this.script.add(script);
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
	
	public class ProcedureObject extends SqlObject {
		@Element(name = "out")
		private List<Out> out = new ArrayList<Out>(); // 存储过程输出

		public List<Out> getOut() {
			return out;
		}

		public void setOut(List<Out> out) {
			this.out = out;
		}

		public class Out {
			@Attribute(name = "name")
			private String name; // 输出参数名称
			
			@Attribute(name = "type")
			private String type; // 输出参数类型

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getType() {
				return type;
			}

			public void setType(String type) {
				this.type = type;
			}

		}
	}

	public class SqlObject {
		@Attribute(name = "name")
		private String name; // SQL(存储过程) 名称
		
		@Attribute(name = "prepare")
		private boolean prepare; // 是否使用预编译SQL

		@Element(name = "choose")
		private List<Choose> choose = new ArrayList<Choose>(); // 选择条件

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

		public List<Choose> getChoose() {
			return choose;
		}

		public void setChooseList(Choose choose) {
			this.choose.add(choose);
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
