package com.auriga.TTApp1.model;

import javax.validation.constraints.NotEmpty;

public class RegistrationOtpDto {
	@NotEmpty(message = "Otp can not be empty")
	private String otp;

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	
}
