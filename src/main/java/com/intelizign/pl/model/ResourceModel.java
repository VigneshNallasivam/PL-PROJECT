package com.intelizign.pl.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.intelizign.pl.utils.CustomFields;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Audited(withModifiedFlag = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "resource_model")
public class ResourceModel 
{

	@JsonView(CustomFields.MyResponseViews.class)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	@NotAudited
	private Long id;
	
	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "emp_name")
	@NotNull
	@NotBlank
	@NotAudited
	private String emp_name;
	
	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "empcode")
	@NotNull
	private String empcode;
	
	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "username")
	@NotNull
	@NotBlank
	@NotAudited
	private String username;
	
	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "email")
	@Email
	@NotAudited
	private String email;
	
	@NotAudited
	@Column(name = "emp_status")
	private String emp_status; 
	
	@NotNull
	@NotBlank
	@NotAudited
	@Column(name = "gender")
	private String gender;
	
	@NotAudited
	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "resource_role")
	private String resource_role;
	
	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "designation")
	@NotAudited
	private String designation;
	
	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "address")
	@NotAudited
	private String address;
	
	@JsonView(CustomFields.MyResponseViews.class)
	@NotAudited
	@Column(name = "location")
	private String location;
	
	@JsonView(CustomFields.MyResponseViews.class)
	@NotAudited
	@Column(name = "branch")
	private String branch;
	
	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "country")
	@NotAudited
	private String country;
	
	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "date_of_join")
	@NotAudited
	private LocalDateTime date_of_join;
	
	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "ctc_revised_date")
	private LocalDateTime ctc_revised_date;
	
	@Column(name = "bench")
	@NotAudited
	private Boolean bench = false;
	
	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "department")
	@NotAudited
	private String department;
	
	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "mobile_number")
	@NotAudited
	private String mobile_number;

	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "report_to")
	@NotAudited
	private String report_to;
	
	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "ctc")
	private Double ctc = 0.0D;
	
	@JsonView(CustomFields.MyResponseViews.class)
	@Column(name = "available_capacity")
	@NotAudited
	private Double available_capacity = 0.0D;

	@Column(name = "active")
	@NotAudited	
	private Boolean active = true;
	
	@NotAudited
	@OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
	@Column(nullable = false)
//	@OrderBy(value = "emp_code")
	private List<ResourceAllocation> resource_alloc_resources;

	@NotAudited
	@ManyToMany(mappedBy = "project_resources")
//	@JsonIgnoreProperties("project_resources")
	private List<ProjectModel> projects;
	
	public ResourceModel(@NotNull @NotBlank String emp_name, @NotNull String empcode,
			@NotNull @NotBlank String username, @Email String email, String emp_status, String gender, String designation, String address, String location, String branch, String country,
			String resource_role, LocalDateTime date_of_join, String department, String mobile_number, String report_to, Double ctc) {
		super();
		this.emp_name = emp_name;
		this.empcode = empcode;
		this.username = username;
		this.email = email;
		this.emp_status = emp_status;
		this.gender = gender;
		this.designation = designation;
		this.address = address;
		this.location = location;
		this.address = address;
		this.country = country;
		this.resource_role = resource_role;
		this.date_of_join = date_of_join;
		this.department = department;
		this.mobile_number = mobile_number;
		this.report_to = report_to;
		this.ctc = ctc;
	}
	
}
