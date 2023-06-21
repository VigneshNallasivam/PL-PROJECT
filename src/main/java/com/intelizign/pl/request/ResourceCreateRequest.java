package com.intelizign.pl.request;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResourceCreateRequest {

	private String emp_name;

	private String empcode;

	private String username;

	private String email;
	
	private String emp_status;
	
	private String role_name;
	
	private String gender;

	private String designation;

	private LocalDateTime date_of_join;

	private String address;
	
	private String location;
	
	private String branch;

	private String country;

	private String department;

	private String mobile_number;

	private String report_to;

	private Double ctc = 0.0D;

	public ResourceCreateRequest(String emp_name, String empcode, String username, String email, String role_name, String designation,
			LocalDateTime date_of_join, String address, String location, String branch, String country, String department, String mobile_number,
			String report_to, Double ctc) {
		super();
		this.emp_name = emp_name;
		this.empcode = empcode;
		this.username = username;
		this.email = email;
		this.role_name = role_name;
		this.designation = designation;
		this.date_of_join = date_of_join;
		this.address = address;
		this.location = location;
		this.branch = branch;
		this.country = country;
		this.department = department;
		this.mobile_number = mobile_number;
		this.report_to = report_to;
		this.ctc = ctc;
	}
}
