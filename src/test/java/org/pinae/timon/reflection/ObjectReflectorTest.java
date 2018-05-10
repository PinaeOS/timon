package org.pinae.timon.reflection;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class ObjectReflectorTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testToList() {
		ObjectReflector refector = new ObjectReflector(Person.class);
		List<Person> people = (List<Person>) refector.toList(ReflectorData.getData(), new String[]{"id", "name", "age", "phone"});
		assertEquals(people.size(), 4);
		
		assertEquals(people.get(0).getName(), "nala");
		assertEquals(people.get(0).getPhone(), "1392997798");
		assertEquals(people.get(0).getAge(), 28);
		
		assertEquals(people.get(1).getName(), "simba");
		assertEquals(people.get(1).getPhone(), "13630183186");
		assertEquals(people.get(1).getAge(), 31);
	}
	
	public static class Person {
		private int id;
		
		private String name;
		
		private int age;
		
		private String phone;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}
		
	}
}
