package com.auriga.TTApp1.dto;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class RegistrationDto {
	@NotEmpty(message = "Name can not be empty")
	@Size(min=2, max=100, message = "Name length should be between 2 to 100 characters.")
    private String name;
     
	@NotEmpty(message = "Email can not be empty")
	@Email(message = "Please provide a valid email id")
	@Size(max=255, message = "Email length should not exceed 255 characters.")
    private String email;
    
	@NotEmpty(message = "Password can not be empty")
	@Size(min=6, message="Password should contain atleast 6 characters.")
    private String password;

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
}
