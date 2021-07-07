package com.auriga.TTApp1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.auriga.TTApp1.exception.ResourceNotFoundException;
import com.auriga.TTApp1.model.Player;
import com.auriga.TTApp1.service.PlayerService;

import javassist.NotFoundException;
import java.lang.Long;

import javax.persistence.EntityNotFoundException;

@Controller
public class PlayerController extends EntityNotFoundException{
	
	@Autowired
    private PlayerService service;

	@GetMapping("/admin/players")
	public String getPlayers() {
		return "admin/players/index";
	}

	@GetMapping("/admin/players/create")
	public String createPlayer() {
		return "admin/players/create";
	}
	
	@RequestMapping(value = "/admin/players/{id}", method = RequestMethod.GET)
	public ModelAndView viewPlayer(@PathVariable("id") Long id){
		Player player = service.get(id).orElseThrow(() -> new ResourceNotFoundException("Player"));
		
		if (player == null) throw new ResourceNotFoundException("Player not found");
		
		ModelAndView modelView = new ModelAndView("admin/players/view");
	    modelView.addObject("player", player);
	    
		return modelView;
	}
}
