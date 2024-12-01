package com.outsider.reward.domain.member.command.application;

import com.outsider.reward.domain.member.exception.MemberException;
import com.outsider.reward.domain.member.exception.MemberErrorCode;
import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.domain.member.command.domain.RefreshToken;
import com.outsider.reward.domain.member.command.domain.RefreshTokenRepository;
import com.outsider.reward.domain.member.command.dto.MemberCommand;
import com.outsider.reward.domain.member.command.dto.TokenDto;
import com.outsider.reward.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.GeneralSecurityException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FileUploadService fileUploadService;
    private final RedisTemplate<String, String> redisTemplate;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    
    @Transactional
    public Long signUp(MemberCommand.SignUp command) {
        if (memberRepository.existsByBasicInfo_Email(command.getEmail())) {
            throw new MemberException(MemberErrorCode.DUPLICATE_EMAIL);
        }
        
        if (memberRepository.existsByBasicInfo_Nickname(command.getNickname())) {
            throw new MemberException(MemberErrorCode.DUPLICATE_NICKNAME);
        }
        
        String verificationCode = redisTemplate.opsForValue().get("EMAIL_VERIFY:" + command.getEmail());
        if (verificationCode == null) {
            throw new MemberException(MemberErrorCode.EMAIL_NOT_VERIFIED);
        }
        
        Member member = Member.builder()
            .name(command.getName())
            .email(command.getEmail())
            .password(passwordEncoder.encode(command.getPassword()))
            .nickname(command.getNickname())
            .build();
            
        member.setEmailVerified(true);
        return memberRepository.save(member).getId();
    }

    @Transactional
    public MemberCommand.LoginResponse login(MemberCommand.Login command) {
        Member member = memberRepository.findByBasicInfo_Email(command.getEmail())
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
            
        if (!passwordEncoder.matches(command.getPassword(), member.getPassword())) {
            throw new MemberException(MemberErrorCode.INVALID_PASSWORD);
        }

        if (!member.isEmailVerified()) {
            throw new MemberException(MemberErrorCode.EMAIL_NOT_VERIFIED);
        }
        
        String token = jwtTokenProvider.createToken(member.getEmail());
        return new MemberCommand.LoginResponse(member.getId(), token);
    }

    @Transactional
    public void verifyEmail(String email, String code) {
        Member member = memberRepository.findByBasicInfo_Email(email)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
            
        if (!emailService.verifyEmail(email, code)) {
            throw new MemberException(MemberErrorCode.INVALID_VERIFICATION_CODE);
        }
        
        member.verifyEmail();
    }

    @Transactional
    public TokenDto refresh(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_REFRESH_TOKEN));
                
        String newAccessToken = jwtTokenProvider.createToken(token.getEmail());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(token.getEmail());
        
        // 기존 토큰 삭제 후 새 토큰 저장
        refreshTokenRepository.deleteById(token.getRefreshToken());
        refreshTokenRepository.save(new RefreshToken(newRefreshToken, token.getEmail()));
        
        return new TokenDto(newAccessToken, newRefreshToken);
    }
    
    @Transactional
    public void logout(String refreshToken, String email) {
        // 리프레시 토큰 검증
        RefreshToken token = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_REFRESH_TOKEN));
        
        // 토큰의 소유자 검증
        if (!token.getEmail().equals(email)) {
            throw new MemberException(MemberErrorCode.UNAUTHORIZED_TOKEN);
        }

        // 검증된 토큰만 삭제
        refreshTokenRepository.deleteById(token.getRefreshToken());
    }

    @Transactional
    public void logoutAll(String email) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByEmail(email);
        refreshTokenRepository.deleteAll(tokens);
    }
    
    @Transactional
    public void updateProfile(Long memberId, MemberCommand.UpdateProfile command) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
                
        if (command.getCurrentPassword() != null) {
            if (!passwordEncoder.matches(command.getCurrentPassword(), 
                    member.getPassword())) {
                throw new MemberException(MemberErrorCode.INVALID_PASSWORD);
            }
            member.updatePassword(passwordEncoder.encode(command.getNewPassword()));
        }
        
        if (command.getNickname() != null) {
            if (memberRepository.existsByBasicInfo_Nickname(command.getNickname())) {
                throw new MemberException(MemberErrorCode.DUPLICATE_NICKNAME);
            }
            member.updateNickname(command.getNickname());
        }
    }
    
    @Transactional
    public String updateProfileImage(Long memberId, MultipartFile file) throws IOException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
                
        String imageUrl = fileUploadService.uploadFile(file);
        member.updateProfileImage(imageUrl);
        
        return imageUrl;
    }

    @Transactional
    public TokenDto handleGoogleCallback(String idToken) {
        log.debug("Starting Google callback handling with token length: {}", idToken.length());
        try {
            // 토큰 검증
            log.debug("Attempting to verify Google ID token...");
            GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
            
            if (googleIdToken == null) {
                log.error("Failed to verify Google ID token - token is null");
                throw new MemberException(MemberErrorCode.INVALID_GOOGLE_TOKEN);
            }

            // 페이로드에서 사용자 정보 추출
            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            
            log.debug("Token payload - Email: {}, Name: {}, Subject: {}, Issuer: {}", 
                email, name, payload.getSubject(), payload.getIssuer());
            log.debug("Token audience: {}", payload.getAudience());
            log.debug("Expected audience: {}", googleIdTokenVerifier.getAudience());

            // 이메일 검증
            if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
                log.error("Email not verified for: {}", email);
                throw new MemberException(MemberErrorCode.EMAIL_NOT_VERIFIED);
            }

            // 회원 조회 또는 생성
            Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.debug("Creating new member for email: {}", email);
                    return createGoogleMember(email, name);
                });

            // 토큰 생성
            String accessToken = jwtTokenProvider.createToken(email);
            String refreshToken = jwtTokenProvider.createRefreshToken(email);

            log.debug("Successfully processed Google login for: {}", email);
            return new TokenDto(accessToken, refreshToken);
        } catch (GeneralSecurityException e) {
            log.error("Security error during token verification", e);
            throw new MemberException(MemberErrorCode.INVALID_GOOGLE_TOKEN);
        } catch (IOException e) {
            log.error("IO error during token verification", e);
            throw new MemberException(MemberErrorCode.INVALID_GOOGLE_TOKEN);
        } catch (Exception e) {
            log.error("Unexpected error during Google auth", e);
            throw new MemberException(MemberErrorCode.GOOGLE_AUTH_FAILED);
        }
    }

    private Member createGoogleMember(String email, String name) {
        Member member = Member.builder()
            .email(email)
            .name(name)
            .nickname(name)
            .password("")
            .build();
        
        member.setOAuthInfo("google", email);
        member.setEmailVerified(true);
        
        return memberRepository.save(member);
    }
} 