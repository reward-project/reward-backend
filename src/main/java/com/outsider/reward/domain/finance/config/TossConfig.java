package com.outsider.reward.domain.finance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.google.api.client.util.Value;

@Configuration
public class TossConfig {
    @Value("${toss.client.api-key}")
    private String apiKey;
    
    @Value("${toss.client.secret-key}")
    private String secretKey;
    
    @Bean
    public RestTemplate tossRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        return restTemplate;
    }
} 