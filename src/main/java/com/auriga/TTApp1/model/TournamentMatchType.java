package com.auriga.TTApp1.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "tournament_match_types")
public class TournamentMatchType {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
	private Tournament tournament;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "match_type_id")
	private MatchType matchType;

	public Tournament getTournament() {
		return tournament;
	}

	public void setTournament(Tournament tournament) {
		this.tournament = tournament;
	}

	public MatchType getMatchType() {
		return matchType;
	}

	public void setMatchType(MatchType matchType) {
		this.matchType = matchType;
	}
}
