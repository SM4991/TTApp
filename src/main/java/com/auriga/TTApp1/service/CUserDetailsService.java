package com.auriga.TTApp1.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.auriga.TTApp1.exception.UserAlreadyExistsException;
import com.auriga.TTApp1.model.CUserDetails;
import com.auriga.TTApp1.model.Role;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.repository.RoleRepository;
import com.auriga.TTApp1.repository.UserRepository;

@Service
public class CUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found");
		}
		return new CUserDetails(user);
	}

	public void register(User user) {
		//Let's check if user already registered with us
        if(checkIfUserExist(user.getEmail())){
            throw new UserAlreadyExistsException("User already exists for this email");
        }
		Role role = roleRepo.findByName("ADMIN");
		
		user.setRole(role);

		encodePassword(user);
		userRepo.save(user);
	}

	public boolean checkIfUserExist(String email) {
		return userRepo.findByEmail(email) != null ? true : false;
	}

	private void encodePassword(User user) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}

}
