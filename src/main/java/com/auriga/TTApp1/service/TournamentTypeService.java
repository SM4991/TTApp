package com.auriga.TTApp1.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auriga.TTApp1.exception.ResourceNotFoundException;
import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.TournamentType;
import com.auriga.TTApp1.repository.TournamentTypeRepository;

@Service
public class TournamentTypeService {
	@Autowired
    private TournamentTypeRepository repo;
	
	public TournamentType get(Long id) {
		return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tournament Type"));
	}
	
	public TournamentType getByTournamentId(Long id, Long tournament_id) {
		TournamentType tournamentType = repo.findByIdAndTournamentId(id, tournament_id);
		if(tournamentType == null) throw new ResourceNotFoundException("Tournament Type");
		return tournamentType;
	}
}
