package com.intelizign.pl.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.intelizign.pl.authentication.UserDetailsImpl;
import com.intelizign.pl.model.FileUploadModel;
import com.intelizign.pl.repositories.FileUploadRepository;
import com.intelizign.pl.response.ResponseHandler;
import com.intelizign.pl.service.FilesStorageServicePath;

@RestController
@RequestMapping("/fileupload")
public class FileUploadController 
{

	@Autowired
	private Environment env;

	@Autowired
	FilesStorageServicePath storageServicepath;

	@Autowired
	private FileUploadRepository fileUploadRepository;

    private static int TenMegaBytes = 30 * 1024 * 1024;
    
	public final Logger LOGGER = LogManager.getLogger(FileUploadController.class);

	// MULTIPLE FILES UPLOADING
	@PostMapping("/upload")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> jdInsert(@RequestParam("file") MultipartFile[] files,Authentication authentication)
	{
		try {
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			if (files != null) 
			{
				List<FileUploadModel> allFiles = new ArrayList<>();
				for (MultipartFile file : files) 
				{
					long file_size = file.getSize();
					if (file_size <= TenMegaBytes) 
					{
						FileUploadModel currentFiles = new FileUploadModel();

						String filename = storageServicepath.save(file);
						currentFiles.setSupporting_files_name(filename);
						currentFiles.setSupporting_files_url(env.getProperty("hostname.name") + "/api/fileupload/attachments?filename=" + filename);
						currentFiles.setMapped(false);
						currentFiles.setUpload_by(userDetails.getEmp_name());
						currentFiles.setUpload_on(LocalDateTime.now(ZoneId.of(env.getProperty("spring.app.timezone"))));
						allFiles.add(currentFiles);
					}
					else 
					{
						return ResponseHandler.generateResponse("Each file size should be less than 10MB",false,HttpStatus.OK, null);
					}
				}
				List<FileUploadModel> fileUploadModel = fileUploadRepository.saveAll(allFiles);
				return ResponseHandler.generateResponse("File Uploaded Successfully", true, HttpStatus.OK,fileUploadModel);
			} 
			else 
			{
				return ResponseHandler.generateResponse("Files or empty", false, HttpStatus.OK, null);
			}
		} 
		catch (Exception e)
		{
			LOGGER.error("Internal Server Error while insert JD: {}", e.getMessage());
			return ResponseHandler.generateResponse("Server Error, Please contact Admin", false, HttpStatus.OK,null);
		}
	}

	//FILE ATTACHMENT
	@GetMapping("/attachments")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Resource> getFile(@RequestParam String filename) 
	{
		try 
		{
			Resource file = storageServicepath.load(filename);
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
					.body(file);
		} 
		catch (Exception e)
		{
			LOGGER.error("Internal Server Error while downloading document: " + e.getMessage());
			return ResponseEntity.notFound().build();
		}

	}

	//SINGLE FILE UPLOADING
	@PostMapping("/singlefileupload")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Object> singleFileUpload(@RequestParam("file") MultipartFile file,Authentication authentication) 
	{
		try 
		{
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			if (userDetails == null)
			{
				return ResponseHandler.generateResponse("Invalid User", false, HttpStatus.OK, null);
			}
			if (file != null) 
			{
				FileUploadModel currentFile = new FileUploadModel();
				String filename = storageServicepath.save(file);
				currentFile.setSupporting_files_name(filename);
				currentFile.setSupporting_files_url(env.getProperty("hostname.name") + "/api/fileupload/attachments/" + filename);
				currentFile.setSupporting_file_view_url(env.getProperty("hostname.name") + "/api/fileupload/viewfile/" + filename);
				currentFile.setUpload_by(userDetails.getEmp_name());
				currentFile.setUpload_on(LocalDateTime.now(ZoneId.of(env.getProperty("spring.app.timezone"))));
				currentFile.setMapped(false);
				FileUploadModel fileUploadModel = fileUploadRepository.save(currentFile);
				return ResponseHandler.generateResponse("File Uploaded Successfully", true, HttpStatus.OK,fileUploadModel);
			}
			else 
			{
				return ResponseHandler.generateResponse("Files or empty", false, HttpStatus.OK, null);
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("Internal Server Error while inserting file: {}", e.getMessage());
			return ResponseHandler.generateResponse("Server Error, Please contact Admin", false, HttpStatus.OK,null);
		}
	}

	//VIEWING UPLOADED FILES
	@GetMapping("/viewfile/{filename:.+}")
	@PreAuthorize("hasAnyAuthority('ADMIN','PRACTICE_HEAD','PROJECT_MANAGER')")
	public ResponseEntity<Resource> viewPDF(@PathVariable String filename) 
	{
		try 
		{
			Resource file = storageServicepath.load(filename);
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
					.contentType(MediaType.parseMediaType("application/pdf")).body(file);
		} 
		catch (Exception e) 
		{
			LOGGER.error("File doesn't exist Error: {}",e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
}
