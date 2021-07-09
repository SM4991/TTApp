package com.auriga.TTApp1.dto;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.auriga.TTApp1.constants.GenderEnum;

public class PlayerDto{
	@NotEmpty(message = "Name can not be empty")
	@Size(min=2, max=100, message = "Name length should be between 2 to 100 characters.")
    private String name;
     
	@NotEmpty(message = "Email can not be empty")
	@Email(message = "Please provide a valid email id")
	@Size(max=255, message = "Email length should not exceed 255 characters.")
    private String email;
	
//	@NotNull(message = "Gender can not be empty.")
	@Enumerated(EnumType.STRING)
	private GenderEnum gender; 
	
	@NotNull(message = "Age can not be empty.")
	private Integer age;
	
    private String image;

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

	public GenderEnum getGender() {
		return gender;
	}

	public void setGender(GenderEnum gender) {
		this.gender = gender;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
