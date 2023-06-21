package com.intelizign.pl.model;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "client_model")
public class ClientModel //PARENT FOR PROJECT
{

	@Column(name = "id",  updatable = false, nullable = false)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Column(name = "clientname",unique = true)
	private String clientname;
	
	@Column(name = "country")
	private String country;
	
	@Column(name = "active")
	private Boolean active = true;

	@Column(name = "address")
	private String address;
	
	@Email
	@NotNull
	@Column(name = "email",unique=true)
	private String email;
	
	@Column(name = "mobile_number")
	private String mobile_number;
	
	@Column(name = "created_on")
	private LocalDateTime created_on;
	
	@Column(name = "created_by")
	private String created_by;
	
	@Column(name = "updated_on")
	private LocalDateTime updated_on;
	
	@Column(name = "updated_by")
	private String updated_by;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "client")
	@Column(nullable = false)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private List<ProjectModel> projects;
}
