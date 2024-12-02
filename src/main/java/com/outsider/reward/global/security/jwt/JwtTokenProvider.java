package com.outsider.reward.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

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
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        String refreshToken = Jwts.builder()
                .claim("sub", email)
                .claim("iat", now)
                .claim("exp", validity)
                .signWith(key)
                .compact();

        refreshTokenRepository.save(new RefreshToken(refreshToken, email));
                
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
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public TokenDto refreshAccessToken(String refreshToken) {
        // 1. 기존 리프레시 토큰 검증
        if (!validateToken(refreshToken)) {
            throw new MemberException(MemberErrorCode.INVALID_REFRESH_TOKEN);
        }
        
        String email = getEmailFromToken(refreshToken);
        
        // 2. DB에서 토큰 확인
        RefreshToken savedToken = refreshTokenRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_REFRESH_TOKEN));
            
        // 3. 새로운 토큰 쌍 발급
        String newAccessToken = createToken(email);
        String newRefreshToken = createRefreshToken(email);
        
        // 4. 기존 토큰 삭제 및 새 토큰 저장
        refreshTokenRepository.deleteById(refreshToken);
        refreshTokenRepository.save(new RefreshToken(newRefreshToken, email));
        
        return new TokenDto(newAccessToken, newRefreshToken);
    }
} 