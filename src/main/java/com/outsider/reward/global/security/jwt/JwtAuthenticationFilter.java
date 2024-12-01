package com.outsider.reward.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
                log.info("Access Token expired, attempting to refresh...");
                String refreshToken = resolveRefreshToken(request);
                
                if (refreshToken != null) {
                    try {
                        TokenDto newTokens = tokenProvider.refreshAccessToken(refreshToken);
                        
                        // 새로운 토큰을 쿠키에 설정
                        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", newTokens.getAccessToken())
                            .path("/")
                            .httpOnly(true)
                            .secure(true)
                            .sameSite("Lax")
                            .build();
                            
                        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", newTokens.getRefreshToken())
                            .path("/")
                            .httpOnly(true)
                            .secure(true)
                            .sameSite("Lax")
                            .build();

                        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
                        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
                        
                        // 새로운 토큰으로 인증 설정
                        Authentication authentication = tokenProvider.getAuthentication(newTokens.getAccessToken());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("Token refreshed and authentication set for user: {}", authentication.getName());
                    } catch (Exception refreshError) {
                        log.error("Failed to refresh token", refreshError);
                    }
                }
            }
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
        
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    log.debug("Access Token found in cookie");
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        // 1. Authorization-Refresh 헤더 확인
        String refreshHeader = request.getHeader("Authorization-Refresh");
        log.debug("Authorization-Refresh Header: {}", refreshHeader);
        
        if (StringUtils.hasText(refreshHeader) && refreshHeader.startsWith("Bearer ")) {
            log.debug("Refresh Token found in header");
            return refreshHeader.substring(7);
        }
        
        // 2. 쿠키에서 확인
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    log.debug("Refresh Token found in cookie");
                    return cookie.getValue();
                }
            }
        }
        
        log.debug("No Refresh Token found");
        return null;
    }
} 