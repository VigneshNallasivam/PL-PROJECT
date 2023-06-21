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

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vertical_model")
public class VerticalModel 
{
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	
	@Column(name = "verticalname",unique = true)
	private String verticalname;
	
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
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "vertical")
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(nullable = false)
	@JsonManagedReference
	private List<ProjectModel> projects; 
}
