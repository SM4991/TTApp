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
import javax.persistence.Table;

import com.auriga.TTApp1.constants.MatchSetStatusEnum;
@Entity
@Table(name="tournament_match_sets")
public class TournamentMatchSet {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private TournamentMatch tournamentMatch;
	
	@Column(nullable = false, length = 20)
	private String name;
	
	@Column(name = "set_number", nullable = false)
	private Integer setNumber;
	
	@Column(name="player1_score", nullable = true)
	private Integer player1Score;
	
	@Column(name="player2_score", nullable = true)
	private Integer player2Score;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private User winner;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private MatchSetStatusEnum status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TournamentMatch getTournamentMatch() {
		return tournamentMatch;
	}

	public void setTournamentMatch(TournamentMatch tournamentMatch) {
		this.tournamentMatch = tournamentMatch;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSetNumber() {
		return setNumber;
	}

	public void setSetNumber(Integer setNumber) {
		this.setNumber = setNumber;
	}

	public Integer getPlayer1Score() {
		return player1Score;
	}

	public void setPlayer1Score(Integer player1Score) {
		this.player1Score = player1Score;
	}

	public Integer getPlayer2Score() {
		return player2Score;
	}

	public void setPlayer2Score(Integer player2Score) {
		this.player2Score = player2Score;
	}

	public User getWinner() {
		return winner;
	}

	public void setWinner(User winner) {
		this.winner = winner;
	}

	public MatchSetStatusEnum getStatus() {
		return status;
	}

	public void setStatus(MatchSetStatusEnum status) {
		this.status = status;
	}
}
