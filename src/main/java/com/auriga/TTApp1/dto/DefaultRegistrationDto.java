package com.auriga.TTApp1.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class DefaultRegistrationDto {
	@NotEmpty(message = "Name can not be empty")
	@Size(min=2, max=100, message = "Name length should be between 2 to 100 characters.")
    private String name;
     
	@NotEmpty(message = "Email can not be empty")
	@Email(message = "Please provide a valid email id")
	@Size(max=255, message = "Email length should not exceed 255 characters.")
    private String email;
    
	@NotEmpty(message = "Password can not be empty")
	@Size(min=6, max=20, message="Password length should be between 6 to 20 characters.")
    private String password;

    private boolean isUsing2FA;
    
    public DefaultRegistrationDto() {
    	super();
    	this.isUsing2FA = false;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean getIsUsing2FA() {
		return isUsing2FA;
	}

	public void setIsUsing2FA(boolean isUsing2FA) {
		this.isUsing2FA = isUsing2FA;
	}
}
