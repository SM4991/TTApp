package com.auriga.TTApp1.filter;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import com.auriga.TTApp1.exception.ResourceBadRequestException;
import com.auriga.TTApp1.model.CUserDetails;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.repository.UserRepository;
import com.auriga.TTApp1.service.UserOtpService;

@Component
public class BeforeAuthenticationFilter
        extends UsernamePasswordAuthenticationFilter {
 
    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private UserOtpService userOtpService;
    
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }
    
    @Autowired
    @Override
    public void setAuthenticationFailureHandler(
            AuthenticationFailureHandler failureHandler) {
        super.setAuthenticationFailureHandler(failureHandler);
    }
     
    @Autowired
    @Override
    public void setAuthenticationSuccessHandler(
            AuthenticationSuccessHandler successHandler) {
        super.setAuthenticationSuccessHandler(successHandler);
    }
    
    public BeforeAuthenticationFilter() {
        setUsernameParameter("email");
        super.setRequiresAuthenticationRequestMatcher(
                new AntPathRequestMatcher("/login", "POST"));
    }
     
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response)
                    throws AuthenticationException {
    	HttpSession session = request.getSession();
    	
    	/* Reset session attributes for login request */
    	resetLoginRequestSessionData(session);
    	
    	String email = request.getParameter("email");
    	
    	session.setAttribute("lemail", email);
        
        User user_obj = userRepo.findActiveByEmail(email);
        
        CUserDetails user_details = new CUserDetails(user_obj);
        
        User user = user_details.getUser();
         
        if (user != null) {
        	session.setAttribute("luser", user);
        	
        	/* If otp is set, attempt authentication */
        	String otp = request.getParameter("password");
            if (otp != null) {
            	session.setAttribute("lotp", otp);
            	
                return super.attemptAuthentication(request, response);            	
            }
             
            
            try {
            	/* Send otp if user found by email */
            	userOtpService.generateOneTimePassword(user);
                throw new InsufficientAuthenticationException("Insufficient OTP");
            } catch (MessagingException | UnsupportedEncodingException ex) {
                throw new AuthenticationServiceException(
                            "Error while sending OTP email.");
            }
        }
         
        return super.attemptAuthentication(request, response);
    }
    
    public void resetLoginRequestSessionData(HttpSession session) {
    	session.setAttribute("lemail", null);
    	session.setAttribute("lotp", null);
    	session.setAttribute("luser", null);
    	session.setAttribute("lerror", null);
    }
}

