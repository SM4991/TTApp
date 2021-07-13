package com.auriga.TTApp1.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.auriga.TTApp1.exception.ResourceNotFoundException;
import com.auriga.TTApp1.model.MatchType;
import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.service.MatchTypeService;
import com.auriga.TTApp1.service.PlayerService;
import com.auriga.TTApp1.service.TournamentService;

@Controller
public class TournamentController {
	@Autowired
	private TournamentService service;
	
	@Autowired
	private MatchTypeService matchTypeService;
	
	@Autowired
	private PlayerService playerService;
	
	@GetMapping(value = {"/admin/tournaments", "/tournaments"})
	public String getTournaments() {
		return "admin/tournaments/index";
	}

	@GetMapping("/admin/tournaments/create")
	public ModelAndView createTournaments() {
		List<MatchType> matchTypes = matchTypeService.getAllForListing();
		ModelAndView modelView = new ModelAndView("admin/tournaments/create");
	    modelView.addObject("matchTypes", matchTypes);
		return modelView;
	}
	
	@RequestMapping(value = {"/admin/tournaments/{id}", "/tournaments/{id}"}, method = RequestMethod.GET)
	public ModelAndView viewTournament(@PathVariable("id") Long id){
		Tournament tournament = service.get(id).orElseThrow(() -> new ResourceNotFoundException("Tournament"));
		
		ModelAndView modelView = new ModelAndView("admin/tournaments/view");
	    modelView.addObject("tournament", tournament);
	    modelView.addObject("matchTypes", tournament.getMatchTypes());
	    
		return modelView;
	}
	
	@GetMapping("/admin/tournaments/{id}/draw")
	public ModelAndView createDraw(@PathVariable("id") Long id) {
		Tournament tournament = service.get(id).orElseThrow(() -> new ResourceNotFoundException("Tournament"));
		
		ModelAndView modelView = new ModelAndView("admin/tournaments/draw");
		
		modelView.addObject("tournament", tournament);
	    modelView.addObject("matchTypes", tournament.getMatchTypes());
	    
	    modelView.addObject("players", playerService.listAll());
	    
		return modelView;
	}
	
	@GetMapping("/admin/tournaments/{id}/fixture")
	public ModelAndView viewFixture(@PathVariable("id") Long id) {
		Tournament tournament = service.get(id).orElseThrow(() -> new ResourceNotFoundException("Tournament"));
		
		ModelAndView modelView = new ModelAndView("admin/tournaments/fixture");
		
		modelView.addObject("tournament", tournament);
	    modelView.addObject("matchTypes", tournament.getMatchTypes());
	    
		return modelView;
	}
	
	@GetMapping(value = {"/admin/tournaments/{id}/matches", "/tournaments/{id}/matches"})
	public ModelAndView viewTournamentMatches(@PathVariable("id") Long id) {
		Tournament tournament = service.get(id).orElseThrow(() -> new ResourceNotFoundException("Tournament"));
		
		ModelAndView modelView = new ModelAndView("admin/tournaments/matches");
		
		modelView.addObject("tournament", tournament);
	    modelView.addObject("matchTypes", tournament.getMatchTypes());
	    
		return modelView;
	}
}
