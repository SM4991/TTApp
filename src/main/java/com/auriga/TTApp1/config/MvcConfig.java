package com.auriga.TTApp1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
	
	@Value("${spring.public.upload.path}")
	private String publicUploadPath;
	
	@Value("${spring.public.upload.folder}")
	private String uploadFolder;
	
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("auth/login_form");
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
          .addResourceHandler("/"+ uploadFolder +"/**")
          .addResourceLocations("file:"+publicUploadPath+"/"+uploadFolder+"/");
        
    }
}
