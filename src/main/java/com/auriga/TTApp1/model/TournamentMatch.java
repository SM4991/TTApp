package com.auriga.TTApp1.model;


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

import org.springframework.data.annotation.Transient;

import com.auriga.TTApp1.constants.MatchStatusEnum;

@Entity
@Table(name="tournament_matches")
public class TournamentMatch {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private TournamentRound tournamentRound;
	
	@Column(nullable = false, length = 255)
	private String name;
		
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private User player1;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private User player2;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private User winner;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = true)
	private MatchStatusEnum status;
	
	@Column(name="match_order", nullable = false)
	private Integer order;
	
	@Column(name="bye_given")
	private Boolean byeGiven;
	
	@Transient
	private String fixtureClass;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TournamentRound getTournamentRound() {
		return tournamentRound;
	}

	public void setTournamentRound(TournamentRound tournamentRound) {
		this.tournamentRound = tournamentRound;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getPlayer1() {
		return player1;
	}

	public void setPlayer1(User player1) {
		this.player1 = player1;
	}

	public User getPlayer2() {
		return player2;
	}

	public void setPlayer2(User player2) {
		this.player2 = player2;
	}

	public User getWinner() {
		return winner;
	}

	public void setWinner(User winner) {
		this.winner = winner;
	}

	public MatchStatusEnum getStatus() {
		return status;
	}

	public void setStatus(MatchStatusEnum status) {
		this.status = status;
	}
	
	public String getStatusText() {
		return MatchStatusEnum.getEnumText(this.status);
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
	
	public Boolean getByeGiven() {
		return byeGiven;
	}

	public void setByeGiven(Boolean byeGiven) {
		this.byeGiven = byeGiven;
	}
	
	public Tournament getTournament() {
		return getTournamentRound().getTournamentType().getTournament();
	}
	
	public String getFixtureClass() {
		return fixtureClass;
	}

	public void setFixtureClass(String fixtureClass) {
		this.fixtureClass = fixtureClass;
	}

	@Override
	public String toString() {
		return "Tournament Match: " + name;
	}
}
