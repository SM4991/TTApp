package com.auriga.TTApp1.model;

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

import com.fasterxml.jackson.annotation.JsonFormat;
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
	@Future(message = "Registration end date must be more than today's date.")
	@JsonFormat(pattern="yyyy-MM-dd")
	@Column(nullable = true, name="reg_end_date")
    private Date regEndDate;
    
	@NotNull(message = "Max Score must not be empty.")
	@Min(value=1, message = "Max score must have value more than or equal to 1.")
    @Column(nullable = false, name="max_score")
    private Integer maxScore;
    
    @Column(nullable = true, length = 255)
    private String image;
    
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "tournaments_match_types",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "match_type_id")
            )
    private List<MatchType> matchTypes;
    
//    @NotNull(message="Select atleast one match type")
    @Transient // Custom column not an entity column
    private List<String> matchTypeIds;
    
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User winner;

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

	public String getStartDate() {
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(startDate);
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getRegEndDate() {
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(regEndDate);
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

	public List<String> getMatchTypeIds() {
		return matchTypeIds;
	}

	public List<MatchType> getMatchTypes() {
		return matchTypes;
	}

	public void setMatchTypes(List<MatchType> matchTypes) {
		this.matchTypes = matchTypes;
	}

	public void setMatchTypeIds(List matchTypeIds) {
		this.matchTypeIds = matchTypeIds;
	}

	public User getWinner() {
		return winner;
	}

	public void setWinner(User winner) {
		this.winner = winner;
	}

	@Override
	public String toString() {
		return "Tournament: " + name;
	}
}
