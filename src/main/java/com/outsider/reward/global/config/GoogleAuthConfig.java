package com.outsider.reward.global.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthConfig {

    @Value("${google.android.client-id}")
    private String androidClientId;

    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier() {
        log.debug("Creating GoogleIdTokenVerifier with client ID: {}", androidClientId);
        
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
            new NetHttpTransport(), 
            GsonFactory.getDefaultInstance())
            .setAudience(Collections.singletonList(androidClientId))
            .setIssuer("https://accounts.google.com")
            .build();
            
        log.debug("GoogleIdTokenVerifier created with audience: {}", verifier.getAudience());
        return verifier;
    }
} 