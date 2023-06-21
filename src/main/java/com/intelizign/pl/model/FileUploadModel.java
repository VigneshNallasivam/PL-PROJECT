package com.intelizign.pl.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "file_upload_model")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadModel 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id",nullable = false,updatable = false)
	private long id;

	@Column(name = "supporting_files_name")
	private String supporting_files_name;

	@Column(name = "supporting_files_url")
	private String supporting_files_url;
	
	@Column(name = "supporting_files_view_url")
	private String supporting_file_view_url;
	    
	@Column(name = "mapped")
	private Boolean mapped;
	
	@Column(name = "upload_by")
	private String upload_by;

	@Column(name = "upload_on")
	private LocalDateTime upload_on;
}
