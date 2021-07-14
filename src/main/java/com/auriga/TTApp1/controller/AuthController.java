package com.auriga.TTApp1.controller;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.auriga.TTApp1.dto.RegistrationDto;
import com.auriga.TTApp1.exception.UserAlreadyExistsException;
import com.auriga.TTApp1.filter.BeforeAuthenticationFilter;
import com.auriga.TTApp1.model.RegistrationOtpDto;
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

	@Autowired
	private BeforeAuthenticationFilter authenticationFilter;

	@GetMapping("/login")
	public String showLoginForm(HttpServletRequest request) {
		authenticationFilter.resetLoginRequestSessionData(request.getSession());
		System.out.println("login form");
		return "auth/login_form";
	}

	@GetMapping("/loginotp")
	public String showLoginOtpForm(HttpServletRequest request, HttpServletResponse response, Model model) {
		if (cUserDetailsService.isAuthenticated()) {
			return "redirect:/admin";
		}

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("luser");

		if (user == null)
			return "redirect:/login";

		return "auth/login_otp_form";
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model, HttpServletRequest request, HttpServletResponse response) {
		if (cUserDetailsService.isAuthenticated()) {
			return "redirect:/admin";
		}

		registrationService.resetRegisterRequestSessionData(request.getSession());
		
		model.addAttribute("registrationForm", new RegistrationDto());

		return "auth/signin_form";
	}

	@PostMapping("/register")
	public String processRegister(@Valid @ModelAttribute("registrationForm") RegistrationDto registrationForm,
			BindingResult bindingResult, Model model, HttpServletRequest request, HttpServletResponse response) {
		if (cUserDetailsService.isAuthenticated()) {
			return "redirect:/admin";
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("registrationForm", registrationForm);
			return "auth/signin_form";
		}
		try {
			User user = registrationService.register(registrationForm);
			
			HttpSession session = request.getSession();
			session.setAttribute("ruser", user);
			
			return "redirect:/registerotp";
		} catch (UserAlreadyExistsException e) {
			bindingResult.rejectValue("email", "registrationForm.email", "An account already exists for this email.");
			model.addAttribute("registrationForm", registrationForm);
			return "auth/signin_form";
		} catch(UnsupportedEncodingException | MessagingException e) {
			bindingResult.rejectValue("email", "registrationForm.email", e.getMessage());
			model.addAttribute("registrationForm", registrationForm);
			return "auth/signin_form";
		}
	}

	@GetMapping("/registerotp")
	public String showRegisterOtpForm(HttpServletRequest request, HttpServletResponse response, Model model) {
		if (cUserDetailsService.isAuthenticated()) {
			return "redirect:/admin";
		}
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("ruser");
		
		if(user == null) return "redirect:/register";
		
		model.addAttribute("registrationOtp", new RegistrationOtpDto());
		
		return "auth/signin_otp_form";
	}

	@PostMapping("/registerotp")
	public String verifyRegisterOtpForm(@Valid @ModelAttribute("registrationOtp") RegistrationOtpDto registrationOtp, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response, Model model) {
		if (cUserDetailsService.isAuthenticated()) {
			return "redirect:/admin";
		}
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("ruser");
		
		if(user == null) return "redirect:/register";

		if(bindingResult.hasErrors()) {
			model.addAttribute("registrationOtp", registrationOtp);
			return "auth/signin_otp_form";
		}
		
		try {
			registrationService.completeRegistration(user, registrationOtp.getOtp());
			
			return "redirect:/login";
		} catch (AuthenticationServiceException e) {
			System.out.println(e.getMessage());
			bindingResult.rejectValue("otp", "registrationOtp.otp", e.getMessage());
			model.addAttribute("registrationOtp", registrationOtp);
			return "auth/signin_otp_form";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		if (cUserDetailsService.isAuthenticated()) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//			String username = auth.getName();

			new SecurityContextLogoutHandler().logout(request, response, auth);
		}

		return "redirect:/login";
	}
}
