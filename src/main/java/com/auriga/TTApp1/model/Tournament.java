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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.auriga.TTApp1.util.DateUtil;
import com.auriga.TTApp1.util.FileUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tournaments")
public class Tournament {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
	@Column(nullable = true, name="start_date")
    private Date startDate;
    
	@Column(nullable = true, name="reg_end_date")
    private Date regEndDate;
    
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
    
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
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
	
//	public Date getStartDate() {
//		return startDate;
//	}

	public String getStartDate() {
		return DateUtil.convertToAppDateFormatString(startDate);
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

//	public Date getRegEndDate() {
//		return regEndDate;
//	}
	
	public String getRegEndDate() {
		return DateUtil.convertToAppDateFormatString(regEndDate);
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

	public List<MatchType> getMatchTypes() {
		return matchTypes;
	}

	public void setMatchTypes(List<MatchType> matchTypes) {
		this.matchTypes = matchTypes;
	}

	public User getWinner() {
		return winner;
	}

	public void setWinner(User winner) {
		this.winner = winner;
	}
	
	/* Check if tournament can be started or not */
	public Boolean getCanTournamentStart() {
		Date startDate = DateUtil.convertToAppDateFormat(this.startDate);
		Date today = DateUtil.formattedToday();
		Integer result = today.compareTo(startDate);
		/* start date greator than or queal to today */
		if(result >= 0) {
			return true;
		}
		return false;
	}
	
	public String getImageUrl() {
		return FileUtil.getTournamentImageUrl(this.getImage());
	}

	@Override
	public String toString() {
		return "Tournament: " + name;
	}
}
