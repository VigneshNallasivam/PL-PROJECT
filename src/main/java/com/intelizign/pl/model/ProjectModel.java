package com.intelizign.pl.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intelizign.pl.request.SupportingFiles;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_model")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ProjectModel 
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	
	@Column(name = "projectname",unique = true)
	private String projectname;
	
	@Column(name = "start_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate start_date;
	
	@Column(name = "end_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate end_date;
	
	@Column(name = "budget")
	private Double budget;
	
	@Column(name = "bit_type")
	private String bit_type;
	
	@Column(name = "working_hours")
	private Double working_hours;
	
	@Type(type = "jsonb")
	@Column(name = "manager_name",columnDefinition = "jsonb")
	private List<String> manager_name;
	
	@Column(name = "project_status")
	private String project_status;

	@Column(name = "resource_id")
	private Long resource_id;
	
	@Column(name = "created_on")
	private LocalDateTime created_on;

	@Column(name = "created_by")
	private String created_by;
	
	@Column(name = "updated_on")
	private LocalDateTime updated_on;
	
	@Column(name = "updated_by")
	private String updated_by;
	
	@Column(name = "active")
	private Boolean active = true;
	
	@Type(type = "jsonb")
	@Column(name = "supporting_files",columnDefinition = "jsonb")
	@Basic(fetch = FetchType.LAZY)
	private List<SupportingFiles> supportingFiles;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@JoinColumn(name = "client_id")
	private ClientModel client;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinTable(name = "project_resources", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "resource_id"))
	private List<ResourceModel> project_resources;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "vertical_id", nullable = false)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonBackReference
	private VerticalModel vertical;
}
