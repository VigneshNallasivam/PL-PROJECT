package com.intelizign.pl.request;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddEmployeeRequest {

	private String emp_name;

	private String emp_code;

	private String username;

	private String email;

	private String designation;

	private String department;

	private String address;

	private String location;
	
	private String branch;
	
	private String country;

	private String mobile_number;

	private List<String> roles;

	public AddEmployeeRequest(String emp_name, String emp_code, String username, String email, String designation,
			String department, String address, String location, String branch, String country, String mobile_number, List<String> roles) {
		super();
		this.emp_name = emp_name;
		this.emp_code = emp_code;
		this.username = username;
		this.email = email;
		this.designation = designation;
		this.department = department;
		this.address = address;
		this.location = location;
		this.branch = branch;
		this.country = country;
		this.mobile_number = mobile_number;
		this.roles = roles;
	}
}
