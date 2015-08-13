package org.pinae.timon.session.pojo;

import org.pinae.timon.reflection.annotation.Column;
import org.pinae.timon.reflection.annotation.Entity;

@Entity(name = "Person")
public class AnnotationPerson {
	
	@Column(name = "id")
	private int userId;
	
	@Column(name = "name")
	private String userName;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "ciry")
	private String city;
	
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
}
