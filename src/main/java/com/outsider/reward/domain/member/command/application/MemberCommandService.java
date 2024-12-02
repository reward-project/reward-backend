package com.outsider.reward.domain.member.command.application;

import com.outsider.reward.domain.member.exception.MemberException;
import com.outsider.reward.domain.member.exception.MemberErrorCode;
import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.domain.member.command.domain.OAuthProvider;
import com.outsider.reward.domain.member.command.domain.RefreshToken;
import com.outsider.reward.domain.member.command.domain.RefreshTokenRepository;
import com.outsider.reward.domain.member.command.domain.Role;
import com.outsider.reward.domain.member.command.domain.RoleRepository;
import com.outsider.reward.domain.member.command.domain.RoleType;
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
import java.util.Optional;

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
    public Long signUp(MemberCommand.SignUp command, RoleType roleType) {
        // 이메일로 기존 회원 조회
        Optional<Member> existingMember = memberRepository.findByBasicInfo_Email(command.getEmail());
        
        if (existingMember.isPresent()) {
            Member member = existingMember.get();
            
            // 이미 해당 역할을 가지고 있는 경우
            if (member.hasRole(roleType)) {
                throw new MemberException(MemberErrorCode.DUPLICATE_ROLE);
            }
            
            // 새로운 역할 추가
            member.addRole(roleType);
            // 변경사항 저장
            memberRepository.save(member); 
            return member.getId();
        }
        
        // 새 회원 생성
        return createNewMember(command, roleType);
    }

    @Transactional
    public TokenDto login(MemberCommand.Login command) {
        Member member = memberRepository.findByBasicInfo_Email(command.getEmail())
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
            
        if (!passwordEncoder.matches(command.getPassword(), member.getPassword())) {
            throw new MemberException(MemberErrorCode.INVALID_PASSWORD);
        }

        if (!member.isEmailVerified()) {
            throw new MemberException(MemberErrorCode.EMAIL_NOT_VERIFIED);
        }
        
        String accessToken = jwtTokenProvider.createToken(member.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());
        
        return new TokenDto(accessToken, refreshToken);
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
    public Member createOAuthMember(String email, String name, String provider, String providerId, RoleType roleType) {
        Member member = Member.createMember(
            email,
            name,
            name,  // nickname
            ""     // password
        );
        
        OAuthProvider oAuthProvider = OAuthProvider.builder()
            .member(member)
            .provider(provider)
            .providerId(providerId)
            .build();
        
        member.addOAuthProvider(oAuthProvider);
        member.verifyEmail();
        member.addRole(roleType);  // Role 대신 RoleType 직접 사용
        
        return memberRepository.save(member);
    }

    @Transactional
    public TokenDto handleGoogleCallback(String idToken, String role) {
        log.debug("Starting Google callback handling with token length: {}, role: {}", 
            idToken.length(), role);
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
            
            log.debug("Token payload - Email: {}, Name: {}, Subject: {}, Role: {}", 
                email, name, payload.getSubject(), role);

            // 이메일 검증
            if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
                log.error("Email not verified for: {}", email);
                throw new MemberException(MemberErrorCode.EMAIL_NOT_VERIFIED);
            }

            // role을 RoleType으로 변환
            RoleType roleType = switch (role.toLowerCase()) {
                case "business" -> RoleType.ROLE_BUSINESS;
                case "admin" -> RoleType.ROLE_ADMIN;
                default -> RoleType.ROLE_USER;
            };

            // 회원 조회 또는 생성
            Member member = memberRepository.findByBasicInfo_Email(email)
                .orElseGet(() -> createOAuthMember(
                    email, 
                    name, 
                    "google", 
                    payload.getSubject(),
                    roleType  // 전달받은 role 사용
                ));

            // 토큰 생성
            String accessToken = jwtTokenProvider.createToken(email);
            String refreshToken = jwtTokenProvider.createRefreshToken(email);

            log.debug("Successfully processed Google login for: {}", email);
            return new TokenDto(accessToken, refreshToken);
        } catch (Exception e) {
            log.error("Unexpected error during Google auth", e);
            throw new MemberException(MemberErrorCode.GOOGLE_AUTH_FAILED);
        }
    }

    @Transactional
    public Long signUpUser(MemberCommand.SignUp command) {
        return signUp(command, RoleType.ROLE_USER);
    }

    @Transactional
    public Long signUpBusiness(MemberCommand.SignUp command) {
        return signUp(command, RoleType.ROLE_BUSINESS);
    }

    @Transactional
    public Long signUpAdmin(MemberCommand.SignUp command) {
        return signUp(command, RoleType.ROLE_ADMIN);
    }

    private Long createNewMember(MemberCommand.SignUp command, RoleType roleType) {
        // 닉네임 중복 체크
        if (memberRepository.existsByBasicInfo_Nickname(command.getNickname())) {
            throw new MemberException(MemberErrorCode.DUPLICATE_NICKNAME);
        }
        
        // 이메일 인증 확인
        String verificationCode = redisTemplate.opsForValue().get("EMAIL_VERIFY:" + command.getEmail());
        if (verificationCode == null) {
            throw new MemberException(MemberErrorCode.EMAIL_NOT_VERIFIED);
        }
        
        // 새 회원 생성
        Member member = Member.createMember(
            command.getEmail(),
            command.getName(),
            command.getNickname(),
            passwordEncoder.encode(command.getPassword())
        );
        
        member.addRole(roleType);  // Role 대신 RoleType 직접 사용
        member.setEmailVerified(true);
        
        return memberRepository.save(member).getId();
    }
} 