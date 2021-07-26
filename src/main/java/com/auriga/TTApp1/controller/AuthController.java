package com.auriga.TTApp1.controller;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.auriga.TTApp1.dto.DefaultRegistrationDto;
import com.auriga.TTApp1.dto.RegistrationDto;
import com.auriga.TTApp1.dto.RegistrationOtpDto;
import com.auriga.TTApp1.exception.ResourceBadRequestException;
import com.auriga.TTApp1.exception.UserAlreadyExistsException;
import com.auriga.TTApp1.filter.BeforeAuthenticationFilter;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.service.CUserDetailsService;
import com.auriga.TTApp1.service.RegistrationService;
import com.auriga.TTApp1.util.SecurityUtil;

@Controller
public class AuthController {

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private CUserDetailsService cUserDetailsService;

	@Autowired
	private BeforeAuthenticationFilter authenticationFilter;

	/* Action to show form to login via email & password or email & otp */
	@GetMapping("/login")
	public String showLoginForm(HttpServletRequest request) {
		if (cUserDetailsService.isAuthenticated()) {
			return "redirect:/admin";
		}
		/* If login, signin via otp enabled, show otp form else default form */
		if(SecurityUtil.isLoginSigninViaOtpEnabled()) {
			return showLoginViaOtpForm(request);
		} else {
			return showDefaultLoginForm(request);
		}
	}
	
	/* Action to show form for email input to login user via email and otp */
	private String showLoginViaOtpForm(HttpServletRequest request) {
		authenticationFilter.resetLoginRequestSessionData(request.getSession());
		return "auth/login_form";
	}
	
	/* Action to show form to login user via email and otp */
	private String showDefaultLoginForm(HttpServletRequest request) {
		return "auth/default_login_form";
	}

	/* Action to show form for otp input to login user via email and otp */
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
	
	/* Action to show form to register via email & password or email & otp */
	@GetMapping("/register")
	public String showRegistrationForm(Model model, HttpServletRequest request, HttpServletResponse response) {
		if (cUserDetailsService.isAuthenticated()) {
			return "redirect:/admin";
		}
		/* If login, signin via otp enabled, show otp form else default form */
		if(SecurityUtil.isLoginSigninViaOtpEnabled()) {
			return showRegistrationViaOtpForm(model, request, response);
		} else {
			return showDefaultRegistrationForm(model, request, response);
		}
	}
	
	/* Action to show form to register user via email and password */
	private String showDefaultRegistrationForm(Model model, HttpServletRequest request, HttpServletResponse response) {
		model.addAttribute("registrationForm", new DefaultRegistrationDto());

		return "auth/default_signin_form";
	}

	/* Action to show form for email input to register user via email and otp */
	private String showRegistrationViaOtpForm(Model model, HttpServletRequest request, HttpServletResponse response) {
		registrationService.resetRegisterRequestSessionData(request.getSession());
		
		model.addAttribute("registrationForm", new RegistrationDto());

		return "auth/signin_form";
	}
	
	/* Action to register user via email and password */
	@PostMapping("/defaultRegister")
	public String processDefaultRegister(@Valid @ModelAttribute("registrationForm") DefaultRegistrationDto registrationForm,
			BindingResult bindingResult, Model model, HttpServletRequest request, HttpServletResponse response) {
		if (cUserDetailsService.isAuthenticated()) {
			return "redirect:/admin";
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("registrationForm", registrationForm);
			return "auth/default_signin_form";
		}
		try {
			User user = registrationService.defaultRegister(registrationForm);
			
			if(user.getIsUsing2FA()) {
				model.addAttribute("qr", cUserDetailsService.generateQRUrl(user));
	            return "auth/qrcode";
			} else {
				return "redirect:/login";
			}
		} catch (UserAlreadyExistsException e) {
			bindingResult.rejectValue("email", "registrationForm.email", "An account already exists for this email.");
			model.addAttribute("registrationForm", registrationForm);
			return "auth/default_signin_form";
		} catch(ResourceBadRequestException | UnsupportedEncodingException | MessagingException e) {
			bindingResult.rejectValue("email", "registrationForm.email", e.getMessage());
			model.addAttribute("registrationForm", registrationForm);
			return "auth/default_signin_form";
		}
	}

	/* Action to register user via email and send verification otp to email */
	@PostMapping("/register")
	public String processRegisterViaOtp(@Valid @ModelAttribute("registrationForm") RegistrationDto registrationForm,
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
		} catch(ResourceBadRequestException | UnsupportedEncodingException | MessagingException e) {
			bindingResult.rejectValue("email", "registrationForm.email", e.getMessage());
			model.addAttribute("registrationForm", registrationForm);
			return "auth/signin_form";
		}
	}

	/* Action to show form for otp input to register user via email and otp */
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

	/* Action to verify otp and complete registration to register user via email and otp */
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
			bindingResult.rejectValue("otp", "registrationOtp.otp", e.getMessage());
			model.addAttribute("registrationOtp", registrationOtp);
			return "auth/signin_otp_form";
		}
	}

	/* Action to logout a logged in user */
	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		if (cUserDetailsService.isAuthenticated()) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			new SecurityContextLogoutHandler().logout(request, response, auth);
		}

		return "redirect:/login";
	}
}
