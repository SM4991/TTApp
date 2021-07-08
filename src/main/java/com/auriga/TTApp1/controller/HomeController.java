package com.auriga.TTApp1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.auriga.TTApp1.repository.UserRepository;
import com.auriga.TTApp1.service.CUserDetailsService;

@Controller
public class HomeController {
	
	@Value("${spring.application.name}")
    String appName;

	@Autowired
	private CUserDetailsService cUserDetailsService;
	
	@GetMapping("/")
    public String welcome() {
		if(cUserDetailsService.isAuthenticated()) {
			return "redirect:/admin";
		}
        return "welcome";
    }
	
	@GetMapping("/admin")
    public String dashboard() {
        return "redirect:/admin/tournaments";
    }
	
//	@GetMapping("/admin/{page_id}")
//    public String viewPage(@PathVariable("page_id") String page_id) {
//    	System.out.println(page_id);
//        return "staticpages/"+page_id;
//    }
}
