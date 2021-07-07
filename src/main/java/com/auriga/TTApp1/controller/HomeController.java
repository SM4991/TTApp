package com.auriga.TTApp1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.auriga.TTApp1.repository.UserRepository;

@Controller
public class HomeController {
	
	@Value("${spring.application.name}")
    String appName;

	@GetMapping("/")
    public String welcome() {
        return "staticpages/welcome";
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
