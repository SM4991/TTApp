package com.auriga.TTApp1.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.auriga.TTApp1.model.Player;
import com.auriga.TTApp1.repository.PlayerRepository;
import com.auriga.TTApp1.service.PaginationService;

@Service
public class PlayerService {
	@Autowired
    private PlayerRepository repo;
	
	@Autowired
    private PaginationService paginationService;
	
    public List<Player> listAll() {
        return repo.findAll();
    }
    
    public PaginationService getAllItems(Integer page, Integer pageSize, String sortBy)
    {
    	Pageable paging = paginationService.pagingData(page, pageSize, sortBy);
 
        Page<Player> pagedResult = repo.findAll(paging);
         
        return paginationService.paginatedItems(pagedResult, page, pageSize, sortBy);
    }
     
    public void save(Player player) {
        repo.save(player);
    }
     
    public Optional<Player> get(Long id) {
        return repo.findById(id);
    }
     
    public void delete(Player player) {
        repo.delete(player);
    }
}
