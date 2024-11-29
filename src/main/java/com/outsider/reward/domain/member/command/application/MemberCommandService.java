package com.outsider.reward.domain.member.command.application;

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
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        
        if (memberRepository.existsByBasicInfo_Nickname(command.getNickname())) {
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
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
            .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));
            
        if (!passwordEncoder.matches(command.getPassword(), member.getBasicInfo().getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        
        return new MemberCommand.LoginResponse(member.getId());
    }
} 