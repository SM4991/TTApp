package com.auriga.TTApp1.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.auriga.TTApp1.constants.RoundTypeEnum;

@Entity
@Table(name="tournament_rounds")
public class TournamentRound {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private TournamentType tournamentType;
	
	@Column(nullable=false, length = 50)
	private String name;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = true)
	private RoundTypeEnum type;
	
	@Column(name="round_order", nullable=false)
	private Integer order;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TournamentType getTournamentType() {
		return tournamentType;
	}

	public void setTournamentType(TournamentType tournamentType) {
		this.tournamentType = tournamentType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RoundTypeEnum getType() {
		return type;
	}

	public void setType(RoundTypeEnum type) {
		this.type = type;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
	
	public Tournament getTournament() {
		return getTournamentType().getTournament();
	}
	
	@Override
	public String toString() {
		return "Tournament Round: " + name;
	}
}
