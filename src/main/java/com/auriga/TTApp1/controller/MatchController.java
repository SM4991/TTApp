package com.auriga.TTApp1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.auriga.TTApp1.constants.MatchStatusEnum;
import com.auriga.TTApp1.exception.ResourceBadRequestException;
import com.auriga.TTApp1.exception.ResourceNotFoundException;
import com.auriga.TTApp1.model.Tournament;
import com.auriga.TTApp1.model.TournamentMatch;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.service.TournamentMatchService;

@Controller
public class MatchController {
	@Autowired
	private TournamentMatchService service;
	
	@RequestMapping(value = {"/admin/matches/{id}", "/matches/{id}"}, method = RequestMethod.GET)
	public ModelAndView viewMatches(@PathVariable("id") Long id){
		TournamentMatch match = service.get(id).orElseThrow(() -> new ResourceNotFoundException("Match"));
		
		if(match.getStatus() == MatchStatusEnum.INACTIVE) throw new ResourceBadRequestException("Match is in inactive state.");
		
		Tournament tournament = match.getTournament();
		
		ModelAndView modelView = new ModelAndView("admin/matches/score");
		modelView.addObject("match", match);
		modelView.addObject("player1", match.getPlayer1());
		
		User player2 = match.getPlayer2();
		player2 = player2 == null ? new User() : player2;
		modelView.addObject("player2", player2);
		modelView.addObject("tournament", tournament);
	    
		return modelView;
	}
}
