#timon#

Timon is database tools for Java

- Easy to integrated to Java application
- Fast Mapper database to object
- Many usefull tools, like sql parser, sql format

## Installation ##

Direct Download (2015/7/23)

The lastest stable is timon-1.1.tar.gz (release notes)

Maven

	<dependency>
	    <groupId>org.pinae</groupId>
	    <artifactId>timon</artifactId>
	    <version>1.1</version>
	</dependency>


## Getting Start ##

demo for Timon:

XML Mapper (sql.xml):

	<?xml version="1.0" encoding="UTF-8" ?>
	
	<global key="table" value="person" />
	
	<mapper namespaces="org.piane.timon">
		<sql name="getPerson">
			select * from :table where 1=1 
			<choose when="id">
				and id = :id
			</choose>
		</sql>
	</mapper>

Java Code:
	
	public class SQLSessionFactoryDemo {

		public static void main(String[] args) {
			SQLSessionFactory sessionFactory = null;
			SQLBuilder builder = null;
			
			try {
				builder = new SQLBuilder()
				sessionFactory = new SQLSessionFactory();
				
				SQLSession session = sessionFactory.getSession();
				
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("id", 1);
				Person person = (Person)session.one(
					builder.getSQLByNameWithParameters("org.piane.timon.getPerson", parameters), 
					Person.class);
					
				session.close();
					
			} catch (IOException e) {
				
			}
		}
	}
	
## Documentation ##

Full documentation is hosted on [HERE](). 
Sources are available in the `docs/` directory.

## License ##

timon is licensed under the Apache License, Version 2.0 See LICENSE for full license text
