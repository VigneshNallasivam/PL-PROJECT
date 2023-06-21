package com.intelizign.pl.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonView;
import com.intelizign.pl.utils.CustomFields;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee_model")
public class EmployeeModel {

	@JsonView(CustomFields.MyResponseViews.class)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "emp_name")
	private String emp_name;

	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "empcode")
	@NotNull
	@NotBlank
	private String empcode;

	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "username")
	@NotNull
	@NotBlank
	private String username;

	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "email")
	@Email
	private String email;

	@Column(name = "password")
	@NotNull
	@NotBlank
	private String password;

	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "designation")
	private String designation;

	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "address")
	private String address;

	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "location")
	private String location;
	
	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "branch")
	private String branch;
	
	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "country")
	private String country;

	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "role_name")
	private String role_name;

	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "department")
	private String department;

	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "date_of_join")
	private LocalDateTime date_of_join;

	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "mobile_number")
	private String mobile_number;

	@Column(name = "resettoken")
	private String resettoken;

	@Column(name = "token_creation_date")
	private LocalDateTime token_creation_date;

	@Column(name = "active")
	private Boolean active = true;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "employee_roles", joinColumns = @JoinColumn(name = "employee_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private List<Role> roles;

	public EmployeeModel(@NotNull @NotBlank String emp_name, @NotNull @NotBlank String empcode,
			@NotNull @NotBlank String username, @Email String email, @NotNull @NotBlank String password,
			String designation, String address, String location, String branch, String country, String department, LocalDateTime date_of_join,
			String mobile_number, List<Role> roles) {
		super();
		this.emp_name = emp_name;
		this.empcode = empcode;
		this.username = username;
		this.email = email;
		this.password = password;
		this.designation = designation;
		this.address = address;
		this.location = location;
		this.branch = branch;
		this.country = country;
		this.department = department;
		this.date_of_join = date_of_join;
		this.mobile_number = mobile_number;
		this.roles = roles;
	}

}
