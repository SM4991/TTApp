package com.auriga.TTApp1.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.auriga.TTApp1.model.MatchType;
import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.TournamentMatchType;
import com.auriga.TTApp1.repository.MatchTypeRepository;
import com.auriga.TTApp1.repository.TournamentRepository;

@Service
public class TournamentService {
	@Autowired
    private TournamentRepository repo;
	
	@Autowired
    private MatchTypeRepository matchTypeRepo;
	
	@Autowired
    private PaginationService paginationService;
	
    public List<Tournament> listAll() {
        return repo.findAll();
    }
    
    public PaginationService getAllItems(Integer page, Integer pageSize, String sortBy)
    {
    	Pageable paging = paginationService.pagingData(page, pageSize, sortBy);
 
        Page<Tournament> pagedResult = repo.findAll(paging);
         
        return paginationService.paginatedItems(pagedResult, page, pageSize, sortBy);
    }
     
    public void save(Tournament tournament) {
        repo.save(tournament);
        
        System.out.println(tournament.getMatchTypeIds());
        /* Save Match Type for Tournament  */
        tournament.getMatchTypeIds().forEach(item->{
        	Long id = Long.valueOf(item);
        	MatchType matchType = matchTypeRepo.getById(id);
        	
        	TournamentMatchType tMatchType = new TournamentMatchType();
        	tMatchType.setTournament(tournament);
        	tMatchType.setMatchType(matchType);
        	
        	matchTypeRepo.save(tMatchType);
        });
    }
     
    public Optional<Tournament> get(Long id) {
        return repo.findById(id);
    }
     
    public void delete(Tournament tournament) {
        repo.delete(tournament);
    }
}
