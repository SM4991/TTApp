package com.auriga.TTApp1.service;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auriga.TTApp1.service.EmailService;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.repository.UserRepository;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class UserOtpService {
	@Autowired UserRepository userRepo;
     
    @Autowired EmailService emailService;
     
    public void generateOneTimePassword(User user) throws UnsupportedEncodingException, MessagingException {
        String OTP = RandomString.make(8);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedOTP= passwordEncoder.encode(OTP);
         System.out.println("Generated Otp:" + OTP);
        user.setOneTimePassword(encodedOTP);
        user.setOtpRequestedTime(new Date());
         
        userRepo.save(user);
         
        sendOTPEmail(user, OTP);
    }
     
    public void sendOTPEmail(User user, String OTP) {
    	String to = user.getEmail();
    	
    	String subject = "Here's your One Time Password (OTP) - Expire in 5 minutes!";
        
        String content = "<p>Hello " + user.getName() + "</p>"
                + "<p>For security reason, you're required to use the following "
                + "One Time Password to login:</p>"
                + "<p><b>" + OTP + "</b></p>"
                + "<br>"
                + "<p>Note: this OTP is set to expire in 5 minutes.</p>";
        
        emailService.sendMessage(to, subject, content);
    }
 
    public void clearOTP(User user) {
    	user.setOneTimePassword(null);
    	user.setOtpRequestedTime(null);
    	userRepo.save(user);  
    }
}
