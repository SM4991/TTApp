package com.auriga.TTApp1.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {
	@Value("${spring.public.upload.path}")
	private String publicUploadPath;
	
	@Value("${spring.public.upload.folder}")
	private String uploadFolder;
	
	public String saveUploadedFile(MultipartFile file, String uploadPath) throws IOException {
        uploadPath = "/"+uploadFolder+uploadPath;
		String fullUploadPath = publicUploadPath+uploadPath;
        
		// Make sure directory exists!
        File uploadDir = new File(fullUploadPath);
        uploadDir.mkdirs();
  
        if (!file.isEmpty()) {
        	String fileName = file.getOriginalFilename();
        	fileName = fileName.replaceAll(" ", "_");
        	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        	fileName = timeStamp+fileName;
        	String uploadFullFilePath = fullUploadPath + "/" + fileName;
        	String uploadFilePath = uploadPath + "/" + fileName;

            byte[] bytes = file.getBytes();
            Path path = Paths.get(uploadFullFilePath);
            Files.write(path, bytes);
            return uploadFilePath;
        }
        
        return "";
    }
}
