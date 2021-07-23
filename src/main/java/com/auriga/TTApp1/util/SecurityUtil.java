package com.auriga.TTApp1.util;

public class SecurityUtil {
	private static String login_signin_via="bascic"; //basic/otp
	
	public static Boolean isLoginSigninViaOtpEnabled() {
		return login_signin_via.equals("otp") ? true : false;
	}
}
