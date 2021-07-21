package com.auriga.TTApp1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.auriga.TTApp1.exception.ResourceNotFoundException;
import com.auriga.TTApp1.model.CUserDetails;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.service.CUserDetailsService;

@Controller
public class UserController {
	@Autowired
    private CUserDetailsService service;
	
	@GetMapping(value = "/admin/users/profile")
	public ModelAndView viewPlayer(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		CUserDetails userDetail = (CUserDetails) auth.getPrincipal();

		User user = userDetail.getUser();
		
		ModelAndView modelView = new ModelAndView("admin/users/profile");
	    modelView.addObject("user", user);
	    
		return modelView;
	}
}
