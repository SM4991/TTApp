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

import com.auriga.TTApp1.constants.TournamentTypeEnum;
import com.auriga.TTApp1.exception.ResourceBadRequestException;
import com.auriga.TTApp1.exception.ResourceNotFoundException;
import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.TournamentType;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.repository.TournamentRoundRepository;
import com.auriga.TTApp1.repository.TournamentTypeRepository;
import com.auriga.TTApp1.service.TournamentTypeService;
import com.auriga.TTApp1.service.PlayerService;
import com.auriga.TTApp1.service.TournamentService;

@Controller
public class TournamentController {
	@Autowired
	private TournamentTypeRepository tournamentTypeRepo;
	
	@Autowired
	private TournamentRoundRepository roundRepo;
	
	@Autowired
	private TournamentService service;
	
	@Autowired
	private TournamentTypeService matchTypeService;
	
	@Autowired
	private PlayerService playerService;
	
	@GetMapping(value = {"/admin/tournaments", "/tournaments"})
	public String getTournaments() {
		return "admin/tournaments/index";
	}

	@GetMapping("/admin/tournaments/create")
	public ModelAndView createTournaments() {
		ModelAndView modelView = new ModelAndView("admin/tournaments/create");
		return modelView;
	}
	
	@RequestMapping(value = {"/admin/tournaments/{id}", "/tournaments/{id}"}, method = RequestMethod.GET)
	public ModelAndView viewTournament(@PathVariable("id") Long id){
		Tournament tournament = service.get(id);
		List<TournamentType> tournamentTypes = tournamentTypeRepo.findByTournament(tournament);
		
		ModelAndView modelView = new ModelAndView("admin/tournaments/view");
		
		modelView.addObject("tournament", tournament);
	    modelView.addObject("tournamentTypes", tournamentTypes);
	    modelView.addObject("forbidCreateDraw", true);
	    
	    tournamentTypes.forEach(type -> {
	    	Long count = roundRepo.countByTournamentType(type);
	    	if(count == 0) {
	    		modelView.addObject("forbidCreateDraw", false);
	    	}
	    });
	    
		return modelView;
	}
	
	@GetMapping("/admin/tournaments/{id}/draw")
	public ModelAndView createDraw(@PathVariable("id") Long id) {
		Tournament tournament = service.get(id);
		
		ModelAndView modelView = new ModelAndView("admin/tournaments/draw");
		
		modelView.addObject("tournament", tournament);
		modelView.addObject("tournamentTypes", tournamentTypeRepo.findByTournament(tournament));
	    modelView.addObject("players", playerService.listAll());
	    
		return modelView;
	}
	
	@GetMapping(value = {"/admin/tournaments/{id}/fixture", "/admin/tournaments/{id}/fixture/{tti}", "/tournaments/{id}/fixture", "/tournaments/{id}/fixture/{tti}"})
	public ModelAndView viewFixture(@PathVariable("id") Long id, @PathVariable(required = false) Long tti) {
		Tournament tournament = service.get(id);
		
		ModelAndView modelView = new ModelAndView("admin/tournaments/fixture");
		
		modelView.addObject("tournament", tournament);
		modelView.addObject("tournamentTypes", tournamentTypeRepo.findByTournament(tournament));
	    modelView.addObject("tournamentTypeId", tti);
	    
		return modelView;
	}
	
	@GetMapping(value = {"/admin/tournaments/{id}/matches", "/admin/tournaments/{id}/matches/{tti}", "/tournaments/{id}/matches", "/tournaments/{id}/matches/{tti}"})
	public ModelAndView viewTournamentMatches(@PathVariable("id") Long id, @PathVariable(required = false) Long tti) {
		Tournament tournament = service.get(id);
		
		ModelAndView modelView = new ModelAndView("admin/tournaments/matches");
		
		modelView.addObject("tournament", tournament);
		modelView.addObject("tournamentTypes", tournamentTypeRepo.findByTournament(tournament));
	    modelView.addObject("tournamentTypeId", tti);
	    
		return modelView;
	}
}
