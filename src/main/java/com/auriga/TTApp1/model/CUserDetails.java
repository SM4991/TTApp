package com.auriga.TTApp1.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CUserDetails implements UserDetails {
 
    private User user;
     
    public CUserDetails(User user) {
        this.user = user;
    }
 
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
 
    @Override
    public String getPassword() {
    	System.out.println("Get password called");
        if (user.isOTPRequired()) {
        	System.out.println("Get password: "+ user.getOneTimePassword());
            return user.getOneTimePassword();
        }
         
        return user.getPassword();
    }
 
    @Override
    public String getUsername() {
		System.out.println("Get username: " + user.getEmail());
        return user.getEmail();
    }
 
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
 
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
 
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
 
    @Override
    public boolean isEnabled() {
        return true;
    }
     
    public String getName() {
        return user.getName();
    }
    
    public User getUser() {
        return user;
    }
 
}
