package com.auriga.TTApp1.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.repository.UserRepository;
import com.auriga.TTApp1.service.UserOtpService;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private UserOtpService userOtpService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		HttpSession session = request.getSession();
		
		User user = (User) session.getAttribute("luser");
		String email = (String) session.getAttribute("lemail");
		String otp = (String) session.getAttribute("lotp");

		String redirectURL = "/login?error";
		if (exception.getMessage().contains("Insufficient OTP")) {
			redirectURL = "/loginotp";
		} else {
			if(user != null && otp != null) {
				redirectURL = "/loginotp?error";
			}
			session.setAttribute("lerror", exception.getMessage());
		}

		super.setDefaultFailureUrl(redirectURL);
		super.onAuthenticationFailure(request, response, exception);
	}

}
