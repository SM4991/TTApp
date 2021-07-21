package com.auriga.TTApp1.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

public class FileUtil {
	private static String tournamentDefaultImage = "/images/blank-profile-picture.png";
	private static String userDefaultImage = "/images/blank-profile-picture.png";
	
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
}
