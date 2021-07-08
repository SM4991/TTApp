package com.auriga.TTApp1.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.auriga.TTApp1.exception.UserAlreadyExistsException;
import com.auriga.TTApp1.model.RegistrationForm;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.repository.UserRepository;
import com.auriga.TTApp1.service.CUserDetailsService;
import com.auriga.TTApp1.service.RegistrationService;

@Controller
public class AuthController {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private CUserDetailsService service;
	
	@Autowired 
	private RegistrationService registrationService;
	
	@Autowired
	private CUserDetailsService cUserDetailsService;

//	@GetMapping("/login")
//	public String showLoginForm() {
//		System.out.println("login form");
//		return "auth/login_form";
//	}
	
	@GetMapping("/loginotp")
	public String showLoginOtpForm(HttpServletRequest request, HttpServletResponse response, Model model) {
		if(cUserDetailsService.isAuthenticated()) {
			return "redirect:/admin";
		}
		
		String email = (String) request.getSession().getAttribute("email");
		if (email == null) {
			return "redirect:/login";
		}
		model.addAttribute("email", email);
		return "auth/login_otp_form";
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		if(cUserDetailsService.isAuthenticated()) {
			return "redirect:/admin";
		}

		model.addAttribute("registrationForm", new RegistrationForm());

		return "auth/signin_form";
	}

	@PostMapping("/register")
	public String processRegister(@Valid RegistrationForm registrationForm, BindingResult bindingResult, Model model) {
		if(cUserDetailsService.isAuthenticated()) {
			return "redirect:/admin";
		}
		
		if(bindingResult.hasErrors()){
			model.addAttribute("registrationForm", registrationForm);
			return "auth/signin_form";
		}
		try {
			registrationService.register(registrationForm);
        }catch (UserAlreadyExistsException e){
            bindingResult.rejectValue("email", "registrationForm.email","An account already exists for this email.");
            model.addAttribute("registrationForm", registrationForm);
            return "auth/signin_form";
        }

		return "redirect:/admin";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		if (cUserDetailsService.isAuthenticated()) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String username = auth.getName();

			new SecurityContextLogoutHandler().logout(request, response, auth);
		}

		return "redirect:/login";
	}
}
