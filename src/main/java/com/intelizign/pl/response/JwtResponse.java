package com.intelizign.pl.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {

	private Long id;
	
	private String emp_name;
	
	private String emp_code;
	
	private String username;
	
	private String email;
	
	private String designation;
	
	private String address;
	
	private String department;
	
	private String mobile_number;
	
	private List<String> roles;


	public JwtResponse(Long id, String emp_name, String emp_code, String username, String email, String designation,
			String address, String department, String mobile_number, List<String> roles) {

		this.id = id;
		this.emp_name = emp_name;
		this.emp_code = emp_code;
		this.username = username;
		this.email = email;
		this.designation = designation;
		this.address = address;
		this.department = department;
		this.mobile_number = mobile_number;
		this.roles = roles;
	}	
}
