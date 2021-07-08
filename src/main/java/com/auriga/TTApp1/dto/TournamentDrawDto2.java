package com.auriga.TTApp1.dto;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import com.auriga.TTApp1.model.MatchType;
import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.User;

public class TournamentDrawDto2 implements Serializable {
    private Tournament tournament;
     
	private MatchType matchType;
    
    private List<User> players;

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

	public List<User> getPlayers() {
		return players;
	}

	public void setPlayers(List<User> players) {
		this.players = players;
	}


}
