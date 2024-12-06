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

import com.outsider.reward.domain.member.command.dto.TokenDto;

import io.jsonwebtoken.ExpiredJwtException;

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

        // Refresh 토큰 엔드포인트 처리
        if (request.getRequestURI().equals("/api/v1/members/refresh")) {
            String refreshToken = request.getHeader("Authorization-Refresh");
            log.info("Refresh Token: {}", refreshToken != null ? "Present" : "Not Present");
            
            if (StringUtils.hasText(refreshToken) && refreshToken.startsWith("Bearer ")) {
                refreshToken = refreshToken.substring(7);
                try {
                    if (tokenProvider.validateToken(refreshToken)) {
                        filterChain.doFilter(request, response);
                        return;
                    }
                } catch (Exception e) {
                    log.error("Refresh Token validation failed", e);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // 기존 Access 토큰 처리
        String jwt = resolveToken(request);
        log.info("JWT Token: {}", jwt != null ? "Present" : "Not Present");
        
        if (StringUtils.hasText(jwt)) {
            try {
                if (tokenProvider.validateToken(jwt)) {
                    Authentication authentication = tokenProvider.getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Authentication set for user: {}", authentication.getName());
                }
            } catch (ExpiredJwtException e) {
                log.info("Access Token expired");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        
        filterChain.doFilter(request, response);
        log.info("=== JWT Filter Completed ===\n");
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 