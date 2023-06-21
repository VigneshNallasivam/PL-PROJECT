package com.intelizign.pl.request;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupportingFiles implements Serializable 
{
	private Long id;
	private String supporting_files_name;
	private String supporting_files_url;
	private String supporting_file_view_url;
	private Boolean mapped;
}