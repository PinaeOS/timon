package org.pinae.timon.reflection;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.pinae.timon.reflection.annotation.Column;
import org.pinae.timon.reflection.annotation.Entity;

public class AnnotationReflectorTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testToList() {
		AnnotationReflector refector = new AnnotationReflector(Person.class);
		List<Person> people = (List<Person>) refector.toList(ReflectorData.getData(), new String[]{"id", "name", "age", "phone"});
		assertEquals(people.size(), 4);
		
		assertEquals(people.get(0).getUserName(), "nala");
		assertEquals(people.get(0).getPhone(), "1392997798");
		assertEquals(people.get(0).getUserAge(), 28);
		
		assertEquals(people.get(1).getUserName(), "simba");
		assertEquals(people.get(1).getPhone(), "13630183186");
		assertEquals(people.get(1).getUserAge(), 31);
	}
	
	@Entity(name = "Person")
	public static class Person {
		@Column(name = "id")
		private int userId;
		
		@Column(name = "name")
		private String userName;
		
		@Column(name= "age")
		private int userAge;
		
		@Column(name = "phone")
		private String phone;

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public int getUserAge() {
			return userAge;
		}

		public void setUserAge(int userAge) {
			this.userAge = userAge;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}
		
	}
}
