package com.auriga.TTApp1.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.TournamentMatch;
import com.auriga.TTApp1.repository.TournamentMatchRepository;
import com.auriga.TTApp1.repository.TournamentRepository;
import com.auriga.TTApp1.repository.TournamentRoundRepository;

@Service
public class TournamentMatchService {
	@Autowired
	private TournamentMatchRepository repo;
	
	@Autowired
	private TournamentRepository tournamentRepo;
	
	@Autowired
	private PaginationService paginationService;

	public List<TournamentMatch> listAll() {
		return repo.findAll();
	}

	public PaginationService getAllItems(Integer status, Integer page, Integer pageSize, String sortBy) {
		Pageable paging = paginationService.pagingData(page, pageSize, sortBy);

		Page<TournamentMatch> pagedResult = repo.findAll(paging);
		
		return paginationService.paginatedItems(pagedResult, page, pageSize, sortBy);
	}
	
	public Optional<TournamentMatch> get(Long id) {
		return repo.findById(id);
	}

	public void delete(TournamentMatch tournamentMatch) {
		repo.delete(tournamentMatch);
	}
}
