package com.auriga.TTApp1.service;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.auriga.TTApp1.dto.RegistrationDto;
import com.auriga.TTApp1.exception.ResourceBadRequestException;
import com.auriga.TTApp1.exception.UserAlreadyExistsException;
import com.auriga.TTApp1.model.CUserDetails;
import com.auriga.TTApp1.model.Role;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.repository.RoleRepository;
import com.auriga.TTApp1.repository.UserRepository;

@Service()
public class RegistrationService {
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired 
	private CUserDetailsService cUserDetailsService;
	
	@Autowired UserOtpService userOtpService;

	public User register(RegistrationDto registrationForm) throws UnsupportedEncodingException, MessagingException {
		//Let's check if user already registered with us
        if(cUserDetailsService.checkIfUserExist(registrationForm.getEmail())){
            throw new UserAlreadyExistsException("User already exists for this email");
        }	
        
		Role role = getAdminRole();
		
		User user = new User();
    	BeanUtils.copyProperties(registrationForm, user);
    	
    	user.setIsLoginActive(false);
		user.setRole(role);

		userRepo.save(user);
		
		/* Send otp if user found by email */
    	userOtpService.generateOneTimePassword(user);
		
		return user;
	}
	
	public void completeRegistration(User user, String otp) {
		Boolean valid = validateOtp(user, otp);
		
		if(valid) {
			user.setIsLoginActive(true);

			userRepo.save(user);
			
			userOtpService.clearOTP(user);
		} else {
			throw new AuthenticationServiceException("Invalid Otp");
		}
	}
	
	public Boolean validateOtp(User user, String otp) {
		String encoded_otp = encodeOtp(otp);
		
		CUserDetails userDetail = new CUserDetails(user);
		
		System.out.println(userDetail.getPassword());
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    	if(passwordEncoder.matches(otp, userDetail.getPassword())) {
    		return true;
    	} else {
    		return false;
    	}
	}
	
	protected String encodeOtp(String otp) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(otp);
		return encodedPassword;
	}
	
	public Role getAdminRole() {
    	Role role =  roleRepo.findByName("ADMIN");
    	if(role == null) throw new ResourceBadRequestException("User role is missing, Please contact the administrator.");
    	return role;
	}

	public void resetRegisterRequestSessionData(HttpSession session) {
    	session.setAttribute("ruser", null);
    }
}
