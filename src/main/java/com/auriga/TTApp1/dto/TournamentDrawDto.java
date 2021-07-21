package com.auriga.TTApp1.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.User;
import com.sun.istack.NotNull;

public class TournamentDrawDto implements Serializable {
	
	@NotNull
    private Long tournamentId;
     
	@NotNull
	private Long tournamentTypeId;
    
	@NotNull
    private List<Long> playerIds;

	public Long getTournamentId() {
		return tournamentId;
	}

	public void setTournamentId(String tournamentId) {
		this.tournamentId = Long.valueOf(tournamentId);
	}

	public Long getTournamentTypeId() {
		return tournamentTypeId;
	}

	public void setTournamentTypeId(String tournamentTypeId) {
		this.tournamentTypeId = Long.valueOf(tournamentTypeId);
	}

	public List<Long> getPlayerIds() {
		return playerIds;
	}

	public void setPlayerIds(List<String> playerIds) {
		List<Long> tPlayerIds = playerIds.stream().map(id -> Long.valueOf(id)).collect(Collectors.toList());
		this.playerIds = tPlayerIds;
	}
    
}
