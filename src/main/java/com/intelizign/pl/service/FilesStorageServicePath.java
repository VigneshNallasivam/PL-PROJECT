package com.intelizign.pl.service;

import java.nio.file.Path;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageServicePath 
{
	public void init();
    public Resource load(String filename);
    public Boolean checkload(String filename);
	public void deleteAll();
	public void delete(String filename);
	public Stream<Path> loadAll();
	public String save(MultipartFile file);
	public void duplicatesave(MultipartFile file, String filename);
}