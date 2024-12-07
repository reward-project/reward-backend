package com.outsider.reward.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class SecurityContextConfig {
    
    @Bean
    public SecurityContext securityContext() {
        return SecurityContextHolder.getContext();
    }
} 