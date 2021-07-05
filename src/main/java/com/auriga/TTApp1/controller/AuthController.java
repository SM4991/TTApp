package com.auriga.TTApp1.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.repository.UserRepository;

@Controller
public class AuthController {

	@Autowired
	private UserRepository userRepo;

//	@GetMapping("/login")
//	public String showLoginForm() {
//		System.out.println("login form");
//		return "auth/login_form";
//	}
	
	@GetMapping("/loginotp")
	public String showLoginOtpForm(HttpServletRequest request, HttpServletResponse response, Model model) {
		String email = (String) request.getSession().getAttribute("email");
		if (email == null) {
			return "redirect:/login";
		}
		model.addAttribute("email", email);
		return "auth/login_otp_form";
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		System.out.println(isAuthenticated());
		model.addAttribute("user", new User());

		return "auth/signin_form";
	}

	@PostMapping("/process_register")
	public String processRegister(User user) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);

		userRepo.save(user);

		return "redirect:/admin";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			String username = auth.getName();

			new SecurityContextLogoutHandler().logout(request, response, auth);
		}

		return "redirect:/login";
	}

	private boolean isAuthenticated() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth);
		if (auth == null) {
			return false;
		}
		return auth.isAuthenticated();
	}
}
