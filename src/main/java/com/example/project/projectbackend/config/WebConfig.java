package com.example.project.projectbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map "/assets/images/**" to the physical location where images are stored
        registry.addResourceHandler("/assets/images/**")
                .addResourceLocations("classpath:/static/assets/images/")
                .setCachePeriod(3600); // Cache for 1 hour (in seconds)
    }
}