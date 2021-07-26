package com.auriga.TTApp1.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.auriga.TTApp1.model.CUserDetails;
import com.auriga.TTApp1.model.User;
import com.auriga.TTApp1.repository.UserRepository;

@Service
public class CUserDetailsService implements UserDetailsService {
	
	@Value("${spring.application.name}")
    private String appName;
	
	public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";

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
	
	/* Update 2 factor authentication required for login or not */
	public User updateUser2FA(boolean use2FA) {
	    Authentication curAuth = SecurityContextHolder.getContext().getAuthentication();
	    CUserDetails currentUserDetails = (CUserDetails) curAuth.getPrincipal();
	    User currentUser = currentUserDetails.getUser();
	    currentUser.setIsUsing2FA(use2FA);
	    currentUser = userRepo.save(currentUser);
	    
	    Authentication auth = new UsernamePasswordAuthenticationToken(
	    		currentUserDetails, currentUserDetails.getPassword(), curAuth.getAuthorities());
	    SecurityContextHolder.getContext().setAuthentication(auth);
	    return currentUser;
	}

	public boolean checkIfUserExist(String email) {
		return userRepo.findByEmail(email) != null ? true : false;
	}

	public Optional<User> get(Long id) {
		return userRepo.findById(id);
	}

	public Boolean isAuthenticated() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (!(auth instanceof AnonymousAuthenticationToken)) {
			return true;
		}
		return false;
	}

	/* Function to generate QR code url for user */
	public String generateQRUrl(User user) throws UnsupportedEncodingException {
		return QR_PREFIX + URLEncoder.encode(String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", appName,
				user.getEmail(), user.getSecret2FA(), appName), "UTF-8");
	}

}
