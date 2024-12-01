package com.outsider.reward.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        log.info("=== JWT Filter Processing ===");
        log.info("Request URI: {}", request.getRequestURI());
        log.info("Request Method: {}", request.getMethod());
        
        String jwt = resolveToken(request);
        log.info("JWT Token: {}", jwt != null ? "Present" : "Not Present");
        
        if (StringUtils.hasText(jwt)) {
            boolean isValid = tokenProvider.validateToken(jwt);
            log.info("Token Validation: {}", isValid ? "Valid" : "Invalid");
            
            if (isValid) {
                Authentication authentication = tokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Authentication set for user: {}", authentication.getName());
            } else {
                log.warn("Invalid JWT token");
            }
        } else {
            log.info("No JWT token found in request");
        }
        
        filterChain.doFilter(request, response);
        log.info("=== JWT Filter Completed ===\n");
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.debug("Authorization Header: {}", bearerToken);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 