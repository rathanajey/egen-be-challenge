package com.userapi.RestUserAPI.model;

import java.util.HashMap;

public class User {
	
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private String dateCreated;
	private String profilePic;
	private HashMap<String, String> address;
	private HashMap<String, String> company;
	
	public User(){
		address = new HashMap<String, String>();
		address.put("street", "-");
		address.put("city", "-");
		address.put("zip", "-");
		address.put("state", "-");
		address.put("country", "-");
		
		company = new HashMap<String, String>();
		company.put("name", "-");
		company.put("website", "-");
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getProfilePic() {
		return profilePic;
	}
	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}
	public HashMap<String, String> getAddress() {
		return address;
	}
	public void setAddress(HashMap<String, String> address) {
		this.address = address;
	}
	public HashMap<String, String> getCompany() {
		return company;
	}
	public void setCompany(HashMap<String, String> company) {
		this.company = company;
	}
	
	
}
