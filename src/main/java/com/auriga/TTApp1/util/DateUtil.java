package com.auriga.TTApp1.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class DateUtil {
	
	/* Convert date to date format for app */
	public static Date convertToAppDateFormat(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");		
		try {
			return dateFormat.parse(dateFormat.format(date));
		} catch (ParseException e) {
			return null;
		}
	}
	
	/* Convert date to date format string for app */
	public static String convertToAppDateFormatString(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");		
		return dateFormat.format(date);
	}
	
	/* Convert date to date format string for app */
	public static Date formattedToday(){
		Date date = new Date();
		return convertToAppDateFormat(date);
	}
	
	public static String formattedTodayString(){
		Date date = new Date();
		return convertToAppDateFormatString(date);
	}
}
