package com.outsider.reward.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.outsider.reward.domain.member.command.domain.RefreshToken;
import com.outsider.reward.domain.member.command.domain.RefreshTokenRepository;
import com.outsider.reward.domain.member.command.dto.TokenDto;
import com.outsider.reward.domain.member.exception.MemberException;
import com.outsider.reward.domain.member.exception.MemberErrorCode;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j  
public class JwtTokenProvider {


    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .claim("sub", email)
                .claim("iat", now)
                .claim("exp", validity)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(String email) {
        log.debug("Creating refresh token for email: {}", email);
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        String refreshToken = Jwts.builder()
                .claim("sub", email)
                .claim("iat", now)
                .claim("exp", validity)
                .signWith(key)
                .compact();

        log.debug("Generated refresh token: {}", refreshToken.substring(0, Math.min(10, refreshToken.length())) + "...");

        try {
            refreshTokenRepository.save(new RefreshToken(refreshToken, email));
            log.debug("Refresh token saved to repository");
            
            // 저장 후 바로 조회해서 확인
            RefreshToken savedToken = refreshTokenRepository.findById(refreshToken)
                .orElse(null);
            log.debug("Verification - Saved token found: {}", savedToken);
        } catch (Exception e) {
            log.error("Error saving refresh token: ", e);
            throw e;
        }
                
        return refreshToken;
    }

    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        log.info("=== Token Validation Started ===");
        try {
            log.info("Attempting to parse and validate token: {}", token.substring(0, Math.min(10, token.length())) + "...");
            
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
                
            log.info("Token validation successful");
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token expired: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        } finally {
            log.info("=== Token Validation Completed ===");
        }
    }

    public TokenDto refreshAccessToken(String refreshToken) {
        log.debug("Refresh token process started with token: {}", refreshToken.substring(0, Math.min(10, refreshToken.length())) + "...");
        
        // Redis에 저장된 모든 토큰 출력
        try {
            Iterable<RefreshToken> allTokens = refreshTokenRepository.findAll();
            log.debug("All tokens in repository:");
            allTokens.forEach(token -> 
                log.debug("Token: {}, Email: {}", 
                    token.getRefreshToken().substring(0, Math.min(10, token.getRefreshToken().length())) + "...", 
                    token.getEmail())
            );
        } catch (Exception e) {
            log.error("Error listing tokens: ", e);
        }

        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            log.error("Refresh token is null or empty");
            throw new MemberException(MemberErrorCode.INVALID_REFRESH_TOKEN);
        }

        try {
            // 1. 리프레시 토큰 형식 검증
            if (!validateToken(refreshToken)) {
                log.error("Refresh token validation failed - Invalid token format or expired");
                throw new MemberException(MemberErrorCode.INVALID_REFRESH_TOKEN);
            }
            
            // 2. DB에서 토큰 확인
            RefreshToken savedToken = refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> {
                    log.error("Refresh token not found in repository: {}", refreshToken.substring(0, Math.min(10, refreshToken.length())) + "...");
                    return new MemberException(MemberErrorCode.INVALID_REFRESH_TOKEN);
                });
            
            String email = savedToken.getEmail();
            log.debug("Saved token found for email: {}", email);
            
            // 3. 새로운 토큰 쌍 발급
            String newAccessToken = createToken(email);
            String newRefreshToken = createRefreshToken(email);
            
            log.debug("New tokens created - Access token: {}, Refresh token: {}", 
                newAccessToken.substring(0, 10) + "...", 
                newRefreshToken.substring(0, 10) + "...");
            
            // 4. 기존 토큰 삭제 및 새 토큰 저장
            refreshTokenRepository.deleteById(refreshToken);
            
            log.debug("Token refresh completed successfully");
            
            return new TokenDto(newAccessToken, newRefreshToken);
        } catch (Exception e) {
            log.error("Error during token refresh: ", e);
            throw e;
        }
    }
} 