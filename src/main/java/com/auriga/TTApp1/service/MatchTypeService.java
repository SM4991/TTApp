package com.auriga.TTApp1.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auriga.TTApp1.model.MatchType;
import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.repository.MatchTypeRepository;

@Service
public class MatchTypeService {
	@Autowired
    private MatchTypeRepository repo;
	
//    public Map<Long, String> getAllForListing() {
//        Map<Long, String> items = new HashMap<>();
//    	repo.findMatchTypesForListing().stream().forEach(model -> items.put(model.getId(), model.getName()));
//        return items;    
//    }
	
	public List<MatchType> getAllForListing() {
    	return repo.findMatchTypesForListing();
    }
}
