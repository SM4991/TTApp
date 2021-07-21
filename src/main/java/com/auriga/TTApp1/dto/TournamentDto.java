package com.auriga.TTApp1.dto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.auriga.TTApp1.constants.TournamentTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class TournamentDto {
	@NotBlank(message = "Name must not be empty.")
	@Size(min = 3, max = 100, message = "Name must contain characters between 3-100.")
    private String name;
    
	@NotNull(message = "Start Date must not be empty.")
	@Future(message = "Start Date must be more than today's date.")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startDate;
    
	@NotNull(message = "Registration end date must not be empty.")
	@Future(message = "Registration end date must be more than today's date.")
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date regEndDate;
    
	@NotNull(message = "Max Score must not be empty.")
	@Min(value=1, message = "Max score must have value more than or equal to 1.")
    private Integer maxScore;
    
    private String image;
    
    @NotNull(message="Select atleast one match type")
    @Transient // Custom column not an entity column
    private List<TournamentTypeEnum> types;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getRegEndDate() {
		return regEndDate;
	}

	public void setRegEndDate(Date regEndDate) {
		this.regEndDate = regEndDate;
	}

	public Integer getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Integer maxScore) {
		this.maxScore = maxScore;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<TournamentTypeEnum> getTypes() {
		return types;
	}

	public void setTypes(List<TournamentTypeEnum> types) {
		this.types = types;
	}
}
