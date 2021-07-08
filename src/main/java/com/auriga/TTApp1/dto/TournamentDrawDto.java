package com.auriga.TTApp1.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.auriga.TTApp1.model.MatchType;
import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.User;

public class TournamentDrawDto implements Serializable {
    private Long tournamentId;
     
	private Long matchTypeId;
    
    private List<Long> playerIds;

	public Long getTournamentId() {
		return tournamentId;
	}

	public void setTournamentId(String tournamentId) {
		this.tournamentId = Long.valueOf(tournamentId);
	}

	public Long getMatchTypeId() {
		return matchTypeId;
	}

	public void setMatchTypeId(String matchTypeId) {
		this.matchTypeId = Long.valueOf(matchTypeId);
	}

	public List<Long> getPlayerIds() {
		return playerIds;
	}

	public void setPlayerIds(List<String> playerIds) {
		List<Long> tPlayerIds = playerIds.stream().map(id -> Long.valueOf(id)).collect(Collectors.toList());
		this.playerIds = tPlayerIds;
	}
    
}
