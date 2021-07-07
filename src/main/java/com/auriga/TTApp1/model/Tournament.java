package com.auriga.TTApp1.model;

import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tournaments")
public class Tournament {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
	@NotBlank(message = "Name must not be empty.")
	@Size(min = 3, max = 100, message = "Name must contain characters between 3-100.")
    @Column(nullable = false, length = 100)
    private String name;
    
	@NotNull(message = "Start Date must not be empty.")
	@Future(message = "Start Date must be more than today's date.")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = true, name="start_date")
    private Date startDate;
    
	@NotNull(message = "Registration end date must not be empty.")
    @DateTimeFormat(iso=ISO.DATE, pattern = "yyyy-MM-dd")
	@Future(message = "Registration end date must be more than today's date.")
	@Column(nullable = true, name="reg_end_date")
    private Date regEndDate;
    
	@NotNull(message = "Max Score must not be empty.")
	@Min(value=1, message = "Max score must have value more than or equal to 1.")
    @Column(nullable = false, name="max_score")
    private Integer maxScore;
    
    @Column(nullable = true, length = 255)
    private String image;
    
    @OneToMany(mappedBy = "tournament", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
    	      CascadeType.REFRESH }, fetch = FetchType.LAZY)
    @JsonIgnore //Stop loading match types in recursion
	private List<TournamentMatchType> matchTypes;
    
    @Transient // Custom column not an entity column
    private List<String> matchTypeIds;

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

	public List<TournamentMatchType> getMatchTypes() {
		return matchTypes;
	}

	public void setMatchTypes(List<TournamentMatchType> matchTypes) {
		this.matchTypes = matchTypes;
	}

	public List<String> getMatchTypeIds() {
		return matchTypeIds;
	}

	public void setMatchTypeIds(List matchTypeIds) {
		this.matchTypeIds = matchTypeIds;
	}
}
