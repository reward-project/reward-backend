package com.outsider.reward.domain.member.command.application;

import com.outsider.reward.common.exception.BusinessException;
import com.outsider.reward.common.exception.ErrorCode;
import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.domain.member.command.domain.RefreshToken;
import com.outsider.reward.domain.member.command.domain.RefreshTokenRepository;
import com.outsider.reward.domain.member.command.dto.MemberCommand;
import com.outsider.reward.domain.member.command.dto.TokenDto;
import com.outsider.reward.global.security.jwt.JwtTokenProvider;
import com.outsider.reward.global.security.oauth.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FileUploadService fileUploadService;
    
    @Transactional
    public Long signUp(MemberCommand.SignUp command) {
        if (memberRepository.existsByBasicInfo_Email(command.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        
        if (memberRepository.existsByBasicInfo_Nickname(command.getNickname())) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }
        
        Member member = Member.builder()
            .name(command.getName())
            .email(command.getEmail())
            .password(passwordEncoder.encode(command.getPassword()))
            .nickname(command.getNickname())
            .build();
            
        return memberRepository.save(member).getId();
    }

    @Transactional
    public MemberCommand.LoginResponse login(MemberCommand.Login command) {
        Member member = memberRepository.findByBasicInfo_Email(command.getEmail())
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
            
        if (!passwordEncoder.matches(command.getPassword(), member.getBasicInfo().getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        if (!member.isEmailVerified()) {
            throw new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED);
        }
        
        String token = jwtTokenProvider.createToken(member.getBasicInfo().getEmail());
        return new MemberCommand.LoginResponse(member.getId(), token);
    }

    @Transactional
    public void verifyEmail(String email, String code) {
        Member member = memberRepository.findByBasicInfo_Email(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
            
        if (!emailService.verifyEmail(email, code)) {
            throw new BusinessException(ErrorCode.INVALID_VERIFICATION_CODE);
        }
        
        member.verifyEmail();
    }

    @Transactional
    public TokenDto refresh(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));
                
        String newAccessToken = jwtTokenProvider.createToken(token.getEmail());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(token.getEmail());
        
        refreshTokenRepository.delete(token);
        refreshTokenRepository.save(new RefreshToken(newRefreshToken, token.getEmail()));
        
        return new TokenDto(newAccessToken, newRefreshToken);
    }
    
    @Transactional
    public void logout(String email) {
        refreshTokenRepository.deleteByEmail(email);
    }
    
    @Transactional
    public void updateProfile(Long memberId, MemberCommand.UpdateProfile command) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
                
        if (command.getCurrentPassword() != null) {
            if (!passwordEncoder.matches(command.getCurrentPassword(), 
                    member.getBasicInfo().getPassword())) {
                throw new BusinessException(ErrorCode.INVALID_PASSWORD);
            }
            member.updatePassword(passwordEncoder.encode(command.getNewPassword()));
        }
        
        if (command.getNickname() != null) {
            if (memberRepository.existsByBasicInfo_Nickname(command.getNickname())) {
                throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
            }
            member.updateNickname(command.getNickname());
        }
    }
    
    @Transactional
    public String updateProfileImage(Long memberId, MultipartFile file) throws IOException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
                
        String imageUrl = fileUploadService.uploadFile(file);
        member.updateProfileImage(imageUrl);
        
        return imageUrl;
    }
} 