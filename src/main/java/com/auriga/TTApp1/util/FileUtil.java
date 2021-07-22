package com.auriga.TTApp1.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class FileUtil {
	private static String publicFilesUploadPath = "/home/auriga/ttapp1";
	private static String uploadFolder = "upload";
	private static String userFilesUploadPath = "/users";
	private static String tournamentFilesUploadPath = "/tournaments";
	private static String tournamentDefaultImage = "/images/blank-profile-picture.png";
	private static String userDefaultImage = "/images/blank-profile-picture.png";
	public static String[] csvTypes = {"text/csv", "application/vnd.ms-excel"};
	public static String[] imageTypes = {"png", "jpeg", "jpg"};
	
	public static String getPublicFilesUploadPath() {
		return publicFilesUploadPath;
	}
	
	public static String getUploadFolder() {
		return uploadFolder;
	}
	
	public static String getUserFilesUploadPath() {
		return userFilesUploadPath;
	}
	
	public static String getTournamentFilesUploadPath() {
		return tournamentFilesUploadPath;
	}
	
	public static String getTournamentImageUrl(String image) {
		if(image != null && !image.isEmpty()) {
			return image;
		} else {
			return getTournamentDefaultImage();
		}
	}
	
	public static String getTournamentDefaultImage() {
		return tournamentDefaultImage;
	}
	
	public static String getUserImageUrl(String image) {
		if(image != null && !image.isEmpty()) {
			return image;
		} else {
			return getUserDefaultImage();
		}
	}
	
	public static String getUserDefaultImage() {
		return userDefaultImage;
	}
	
	/* Check if file has csv/excel format */
	public static boolean hasCSVFormat(MultipartFile file) {
	    if (Arrays.asList(csvTypes).contains(file.getContentType())) {
	      return true;
	    }

	    return false;
	}
	
	/* Check if file has image format */
	public static boolean hasImageFormat(MultipartFile file) {
	    if (Arrays.asList(imageTypes).contains(file.getContentType())) {
	      return true;
	    }

	    return false;
	}
}
