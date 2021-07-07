package com.auriga.TTApp1.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.*;

@Entity
@Table(name = "players")
public class Player {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
	@NotBlank
	@Size(min = 3, max = 100)
    @Column(nullable = false, length = 100)
    private String name;
    
	@NotBlank
	@Email
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    
	@NotBlank
    @Column(nullable = false, length = 10)
    private String gender;
    
    @Column(nullable = true)
    private Integer age;
    
    @Column(nullable = true, length = 255)
    private String image;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
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
