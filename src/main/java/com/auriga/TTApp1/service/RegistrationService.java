package com.auriga.TTApp1.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.auriga.TTApp1.exception.UserAlreadyExistsException;
import com.auriga.TTApp1.model.RegistrationForm;
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

	public void register(RegistrationForm registrationForm) {
		//Let's check if user already registered with us
        if(cUserDetailsService.checkIfUserExist(registrationForm.getEmail())){
            throw new UserAlreadyExistsException("User already exists for this email");
        }	
        
		Role role = getAdminRole();
		
		User user = new User();
    	BeanUtils.copyProperties(registrationForm, user);
    	
    	user.setIsLoginActive(true);
		user.setRole(role);

		cUserDetailsService.encodePassword(user);
		userRepo.save(user);
	}
	
	public Role getAdminRole() {
    	return roleRepo.findByName("ADMIN");
    }
}
