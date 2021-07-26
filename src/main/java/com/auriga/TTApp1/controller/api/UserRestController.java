package com.auriga.TTApp1.controller.api;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.service.CUserDetailsService;

@RestController
public class UserRestController {
	@Autowired
    private CUserDetailsService service;
	
	@PostMapping("/admin/api/users/update/2fa")
	public String modifyUser2FA(@RequestParam boolean use2FA) 
	  throws UnsupportedEncodingException {
	    User user = service.updateUser2FA(use2FA);
	    if (use2FA) {
	        return service.generateQRUrl(user);
	    }
	    return "";
	}
}
