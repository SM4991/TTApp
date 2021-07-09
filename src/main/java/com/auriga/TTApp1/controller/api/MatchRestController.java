package com.auriga.TTApp1.controller.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.auriga.TTApp1.constants.MatchSetStatusEnum;
import com.auriga.TTApp1.constants.MatchStatusEnum;
import com.auriga.TTApp1.exception.ResourceBadRequestException;
import com.auriga.TTApp1.exception.ResourceNotFoundException;
import com.auriga.TTApp1.model.MatchType;
import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.TournamentMatch;
import com.auriga.TTApp1.model.TournamentMatchSet;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.repository.TournamentMatchRepository;
import com.auriga.TTApp1.service.TournamentMatchService;
import com.auriga.TTApp1.service.TournamentMatchSetService;
import com.fasterxml.jackson.databind.util.JSONPObject;

@RestController
public class MatchRestController {
	@Autowired
	private TournamentMatchRepository repo;
	
	@Autowired
	private TournamentMatchService service;
	
	@Autowired
	private TournamentMatchSetService setService;
	
	@RequestMapping(value = {"/admin/api/matches/{id}", "/api/matches/{id}"}, method = RequestMethod.GET)
	public ModelAndView viewMatch(@PathVariable("id") Long id){
		TournamentMatch match = service.get(id).orElseThrow(() -> new ResourceNotFoundException("Match"));
		
		if(match.getStatus() == MatchStatusEnum.INACTIVE) throw new ResourceBadRequestException("Match is in inactive state.");

		ModelAndView modelView = new ModelAndView("admin/matches/score_form");
		
		Tournament tournament = match.getTournament();
		
		List<TournamentMatchSet> sets = setService.listAllByMatch(match);
		
		Integer diff = 3 - sets.size();
		
		modelView.addObject("match", match);
		modelView.addObject("sets", sets);
		modelView.addObject("diff", diff);
		if(match.getWinner() != null) {
			modelView.addObject("winner", match.getWinner());
		} else {
			sets.forEach(set -> {
				if(set.getStatus() == MatchSetStatusEnum.ONGOING) {
					modelView.addObject("actionSet", set);
				} 
				
				if(set.getStatus() == MatchSetStatusEnum.COMPLETE) {
					modelView.addObject("completeSet", set);
					modelView.addObject("nextSet", set.getSetNumber()+1);
				}
			});
		}

		return modelView;
	}
	
	@RequestMapping(value = {"/admin/api/matches/startSet/{id}/{set}"}, method = RequestMethod.POST)
	public ResponseEntity<Object> startSet(@PathVariable("id") Long id, @PathVariable("set") Integer set){
		TournamentMatch match = service.get(id).orElseThrow(() -> new ResourceNotFoundException("Match"));
		
		if(match.getStatus() == MatchStatusEnum.INACTIVE) throw new ResourceBadRequestException("Match is in inactive state.");

		if(match.getWinner() != null) throw new ResourceBadRequestException("Match is already complete.");
		
		setService.startSet(match, set);
		return new ResponseEntity<>("Set has started.", HttpStatus.OK);
	}
	
	@RequestMapping(value = {"/admin/api/matches/updateScore/{id}"}, method = RequestMethod.POST)
	public ResponseEntity<Object> updateScore(@PathVariable("id") Long id, @RequestParam("player") Integer player, @RequestParam("state") Boolean state){
		TournamentMatchSet set = setService.get(id).orElseThrow(() -> new ResourceNotFoundException("Set"));
		
		if(set.getStatus() == MatchSetStatusEnum.COMPLETE) throw new ResourceBadRequestException("Set is already finished.");

		Map<String, Integer> result = setService.updateScore(set, player, state);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
