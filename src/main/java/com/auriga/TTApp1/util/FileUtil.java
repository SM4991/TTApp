package com.auriga.TTApp1.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

public class FileUtil {
	private static String tournamentDefaultImage = "/images/blank-profile-picture.png";
	private static String playerDefaultImage = "/images/blank-profile-picture.png";
	
	public static String getTournamentImageUrl(String image) {
		if(!image.isEmpty() && image != null) {
			return image;
		} else {
			return getTournamentDefaultImage();
		}
	}
	
	public static String getTournamentDefaultImage() {
		return tournamentDefaultImage;
	}
	
	public static String getPlayerImageUrl(String image) {
		if(!image.isEmpty() && image != null) {
			return image;
		} else {
			return getTournamentDefaultImage();
		}
	}
	
	public static String getPlayerDefaultImage() {
		return playerDefaultImage;
	}
}
