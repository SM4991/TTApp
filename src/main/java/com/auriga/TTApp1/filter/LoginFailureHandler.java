package com.auriga.TTApp1.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		System.out.println("Exception generated: " + exception);
		
		String email = request.getParameter("email");
		request.setAttribute("email", email);
		System.out.println("Failure Handler email: " + email);
		
		String otp = (String) request.getParameter("otp");
		if (otp != null && otp != "") {
			request.setAttribute("otp", otp);
		}
		System.out.println("Failure Handler otp: " + otp);

		String redirectURL = "/login?error";
		if (exception.getMessage().contains("OTP")) {
			redirectURL = "/loginotp";
		} else {
			User user= userRepo.findByEmail(email);
			if (user != null && user.isOTPRequired()) {
				redirectURL = "/loginotp";
			}
		}

		request.getSession().setAttribute("email", email);
		super.setDefaultFailureUrl(redirectURL);
		super.onAuthenticationFailure(request, response, exception);
	}

}
