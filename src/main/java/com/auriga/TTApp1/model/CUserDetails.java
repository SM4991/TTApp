package com.auriga.TTApp1.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.auriga.TTApp1.exception.ResourceBadRequestException;

public class CUserDetails implements UserDetails {
 
    private User user;
     
    public CUserDetails(User user) {
        this.user = user;
    }
 
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = user.getRole();
        List<SimpleGrantedAuthority> authorities = new ArrayList();
         
        authorities.add(new SimpleGrantedAuthority(role.getName()));
         
        return authorities;
    }
 
    @Override
    public String getPassword() {
    	if (user.isOTPExpired()) throw new AuthenticationServiceException("OTP is expired");
        
        return user.getOneTimePassword();
        
    }
 
    @Override
    public String getUsername() {
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
