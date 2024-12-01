package com.outsider.reward.global.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.access-token-validity:3600000}")  // 기본값 1시간
    private long accessTokenValidity;
    
    @Value("${jwt.refresh-token-validity:1209600000}")  // 기본값 14일
    private long refreshTokenValidity;
    
    @Bean
    public SecretKey jwtSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
    
    @Bean
    public long accessTokenValidityInMilliseconds() {
        return accessTokenValidity;
    }
    
    @Bean
    public long refreshTokenValidityInMilliseconds() {
        return refreshTokenValidity;
    }
}