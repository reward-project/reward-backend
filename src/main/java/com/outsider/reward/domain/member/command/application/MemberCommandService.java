package com.outsider.reward.domain.member.command.application;

import com.outsider.reward.common.exception.BusinessException;
import com.outsider.reward.common.exception.ErrorCode;
import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.domain.member.command.dto.MemberCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    
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
        
        return new MemberCommand.LoginResponse(member.getId());
    }
} 