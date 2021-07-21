package com.auriga.TTApp1.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.auriga.TTApp1.constants.TournamentTypeEnum;

@Entity
@Table(name = "tournament_types")
public class TournamentType {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Tournament tournament;
	
	@Enumerated(EnumType.STRING)
	private TournamentTypeEnum type;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private User winner;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Tournament getTournament() {
		return tournament;
	}

	public void setTournament(Tournament tournament) {
		this.tournament = tournament;
	}

	public TournamentTypeEnum getType() {
		return type;
	}
	
	public String getTypeText() {
		return type.getDisplayValue();
	}

	public void setType(TournamentTypeEnum type) {
		this.type = type;
	}

	public User getWinner() {
		return winner;
	}

	public void setWinner(User winner) {
		this.winner = winner;
	}

	@Override
	public String toString() {
		return "TournamentMatchType: " + type;
	}
}
