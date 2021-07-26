package com.auriga.TTApp1.filter;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.auriga.TTApp1.model.CUserDetails;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.repository.UserRepository;
import com.auriga.TTApp1.service.UserOtpService;
import com.auriga.TTApp1.util.SecurityUtil;

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
    	/* If login, signin via otp enabled, 
    	 * then attempt authentication via email and otp, 
    	 * else attempt authentication via email and password */
    	if(SecurityUtil.isLoginSigninViaOtpEnabled()) {
    		HttpSession session = request.getSession();
        	
        	/* Reset session attributes for login request */
        	resetLoginRequestSessionData(session);
        	
        	String email = request.getParameter("email");
        	
        	session.setAttribute("lemail", email);
            
            User user_obj = userRepo.findActiveByEmail(email);
             
            if (user_obj != null) {
                CUserDetails user_details = new CUserDetails(user_obj);
                
                User user = user_details.getUser();
                
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
    	} else {
    		/* Check 2 factor google auth code, if is enabled for user */
    		String email = request.getParameter("email");
    		
    		User user_obj = userRepo.findActiveByEmail(email);
            
            if (user_obj != null) {
            	CUserDetails user_details = new CUserDetails(user_obj);
                
                User user = user_details.getUser();
                
            	String verificationCode = request.getParameter("code");
            	
            	if (user.getIsUsing2FA()) {
                    Totp totp = new Totp(user.getSecret2FA());
                    if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
                        throw new BadCredentialsException("Invalid verfication code");
                    }
                }
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
    
    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}

