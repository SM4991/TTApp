package com.auriga.TTApp1.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.auriga.TTApp1.model.CUserDetails;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.service.UserOtpService;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
 
    @Autowired
    private UserOtpService userOtpService;
     
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
                throws IOException, ServletException {
         
         
        CUserDetails userDetails
                = (CUserDetails) authentication.getPrincipal();
                 
        User user = userDetails.getUser();
         
        if (user.isOTPRequired()) {
            userOtpService.clearOTP(user);
        }
         
        super.onAuthenticationSuccess(request, response, authentication);
    }
 
}