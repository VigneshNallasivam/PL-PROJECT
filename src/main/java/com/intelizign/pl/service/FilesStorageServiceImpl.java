package com.intelizign.pl.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FilesStorageServiceImpl implements FilesStorageServicePath
{
	private final Path fileStorageLocation;

	@Autowired
	public FilesStorageServiceImpl(Environment env) 
	{
		this.fileStorageLocation = Paths.get(env.getProperty("app.file.upload-dir")).toAbsolutePath().normalize();

		try 
		{
			Files.createDirectories(this.fileStorageLocation);
		} 
		catch (Exception ex) 
		{
			throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
		}
	}

	private String getFileExtension(String fileName) 
	{
		if (fileName == null) 
		{
			return null;
		}
		String[] fileNameParts = fileName.split("\\.");
		return fileNameParts[fileNameParts.length - 1];
	}

	@Override
	public String save(MultipartFile file) 
	{
		// Normalize file name
		String fileName = FilenameUtils.removeExtension(file.getOriginalFilename()) + "-" + new Date().getTime() + "."
				+ getFileExtension(file.getOriginalFilename());

		try
		{
			// Check if the filename contains invalid characters
			if (fileName.contains("..")) 
			{
				throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
			}
			//Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return fileName;
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
		}
	}

	@Override
	public void duplicatesave(MultipartFile file, String filename) 
	{
		try 
		{

			Files.copy(file.getInputStream(), this.fileStorageLocation.resolve(filename));
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
		}
	}

	@Override
	public Resource load(String filename) 
	{
		try 
		{
			Path file = fileStorageLocation.resolve(filename);

			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable())
			{
				return resource;
			} 
			else 
			{
				throw new RuntimeException("Could not read the file!");
			}
		} 
		catch (MalformedURLException e) 
		{
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}

	@Override
	public Boolean checkload(String filename)
	{
		try {
			Path file = fileStorageLocation.resolve(filename);
			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) 
			{
				return true;
			} 
			else 
			{
				return false;
			}
		} catch (MalformedURLException e) 
		{
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}

	@Override
	public void delete(String filename) 
	{
		try 
		{
			FileSystemUtils.deleteRecursively(fileStorageLocation.resolve(filename));
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Delete Error: " + e.getMessage());
		}
	}

	@Override
	public void deleteAll() 
	{
		FileSystemUtils.deleteRecursively(fileStorageLocation.toFile());
	}

	@Override
	public Stream<Path> loadAll() 
	{
		try 
		{
			return Files.walk(this.fileStorageLocation, 1).filter(path -> !path.equals(this.fileStorageLocation))
					.map(this.fileStorageLocation::relativize);
		} 
		catch (IOException e) 
		{
			throw new RuntimeException("Could not load the files!");
		}
	}

	@Override
	public void init()
	{
		
	}
}