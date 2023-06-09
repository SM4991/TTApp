package com.auriga.TTApp1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.auriga.TTApp1.filter.BeforeAuthenticationFilter;
import com.auriga.TTApp1.filter.LoginFailureHandler;
import com.auriga.TTApp1.filter.LoginSuccessHandler;
import com.auriga.TTApp1.service.CUserDetailsService;
 
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private BeforeAuthenticationFilter beforeAuthenticationFilter;
    
    @Autowired
    private LoginSuccessHandler loginSuccessHandler;
     
    @Autowired
    private LoginFailureHandler loginFailureHandler;
     
    @Bean
    public UserDetailsService userDetailsService() {
        return new CUserDetailsService();
    }
     
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
     
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
 
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }
 
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
//    @Bean
//    public AuthenticationFailureHandler authenticationFailureHandler() {
//        return new LoginFailureHandler();
//    }
    
//    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
//    @Override
//    public AuthenticationSuccessHandler authenticationSuccessHandlerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        	.authorizeRequests()
	        	.antMatchers("/admin/**").hasAnyAuthority("ADMIN")
	        	.antMatchers("/login").permitAll()
	        	.anyRequest().permitAll()
	        	.and()
	        .addFilterBefore(beforeAuthenticationFilter, BeforeAuthenticationFilter.class)
	        .formLogin()
	            .loginPage("/login")
	            .successHandler(loginSuccessHandler)
	            .failureHandler(loginFailureHandler)
	            .usernameParameter("email")
	            .defaultSuccessUrl("/admin/tournaments")
	            .permitAll()
	            .and()
            .logout()
            	.logoutSuccessUrl("/").permitAll();
    }
}