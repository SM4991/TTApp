package com.auriga.TTApp1.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findActiveByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found");
		}

		return new CUserDetails(user);
	}

	public boolean checkIfUserExist(String email) {
		return userRepo.findByEmail(email) != null ? true : false;
	}
	
	public Optional<User> get(Long id) {
        return userRepo.findById(id);
    }

	protected void encodePassword(User user) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}
	
	public Boolean isAuthenticated() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (!(auth instanceof AnonymousAuthenticationToken)) {
			return true;
		}
		return false;
	}
	
}
