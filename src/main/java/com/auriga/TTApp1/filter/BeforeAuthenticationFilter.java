package com.auriga.TTApp1.filter;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    	String email = request.getParameter("email");
        
        User user_obj = userRepo.findByEmail(email);
        
        CUserDetails user_details = new CUserDetails(user_obj);
        
        User user = user_details.getUser();
         
        if (user != null) {
        	String otp = request.getParameter("password");
//        	System.out.println("request url:" + request.getRequestURI());
            if (otp != null) {
                return super.attemptAuthentication(request, response);
            }
             
            try {
//            	System.out.println("generate otp");
            	userOtpService.generateOneTimePassword(user);
                throw new InsufficientAuthenticationException("OTP");
            } catch (MessagingException | UnsupportedEncodingException ex) {
                throw new AuthenticationServiceException(
                            "Error while sending OTP email.");
            }
        }
         
        return super.attemptAuthentication(request, response);
    }
}

