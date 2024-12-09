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

        String jwt = resolveToken(request);
        log.info("JWT Token: {}", jwt != null ? "Present" : "Not Present");
        
        if (StringUtils.hasText(jwt)) {
            try {
                log.info("Validating JWT token...");
                if (tokenProvider.validateToken(jwt)) {
                    Authentication authentication = tokenProvider.getAuthentication(jwt);
                    log.info("Authentication object created: {}", authentication);
                    log.info("Principal type: {}", authentication.getPrincipal().getClass().getName());
                    log.info("Authorities: {}", authentication.getAuthorities());
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Authentication set in SecurityContext for user: {}", authentication.getName());
                }
            } catch (ExpiredJwtException e) {
                log.error("Token expired: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (Exception e) {
                log.error("Authentication error occurred", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            log.info("No JWT token found in request");
        }
        
        filterChain.doFilter(request, response);
        
        // SecurityContext의 최종 상태 확인
        Authentication finalAuth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Final Authentication state: {}", finalAuth != null ? 
            "Present for user: " + finalAuth.getName() : "Not present");
        
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