package com.auriga.TTApp1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.auriga.TTApp1.util.FileUtil;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/login").setViewName("auth/login_form");
//    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
          .addResourceHandler("/"+ FileUtil.getUploadFolder() +"/**")
          .addResourceLocations("file:"+FileUtil.getPublicFilesUploadPath()+"/"+FileUtil.getUploadFolder()+"/");
        
    }
}
