package com.intelizign.pl.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "resource_allocation_model")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceAllocation {

	@Column(name = "id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "resource_name")
	private String resource_name;
	
	@Column(name = "emp_code")
	private String emp_code;
	
	@Column(name = "emp_name")
	private String emp_name;
	
	@Column(name = "project_id")
	private String project_id;
	
	@Column(name = "project_name")
	private String project_name;

	@Column(name = "manager_name")
	private String manager_name;
	
	@Column(name = "allocated_capacity")
	private Double allocated_capacity;
	
	@Column(name = "active")
	private Boolean active = true;
	
	@Column(name = "created_on")
	private LocalDateTime created_on;
	
	@Column(name = "created_by")
	private String created_by;
	
	@Column(name = "updated_on")
	private LocalDateTime updated_on;
	
	@Column(name = "updated_by")
	private String updated_by;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "resource_id")
	private ResourceModel resource;
}
