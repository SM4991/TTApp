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

import com.auriga.TTApp1.util.FileUtil;

@Service
public class FileUploadService {
	
	@Value("${public.files.upload.path}")
	private String publicFilesUploadPath;
	
	/* Save uploaded file to destination folder */
	public String saveUploadedFile(MultipartFile file, String uploadPath) throws IOException {
        uploadPath = "/"+FileUtil.getUploadFolder()+uploadPath;
		String fullUploadPath = publicFilesUploadPath+uploadPath;
        
		// Make sure directory exists!
        File uploadDir = new File(fullUploadPath);
        uploadDir.mkdirs();
  
        if (!file.isEmpty()) {
        	String fileName = getFileName(file);

        	String uploadFullFilePath = fullUploadPath + "/" + fileName;
        	String uploadFilePath = uploadPath + "/" + fileName;

            byte[] bytes = file.getBytes();
            Path path = Paths.get(uploadFullFilePath);
            Files.write(path, bytes);
            return uploadFilePath;
        }
        
        return "";
    }
	
	/* Get file name to save file as in folder */
	public String getFileName(MultipartFile file) {
		String fileName = file.getOriginalFilename();
    	fileName = fileName.replaceAll(" ", "_");
    	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    	fileName = timeStamp+fileName;
    	
    	return fileName;
	}
}
