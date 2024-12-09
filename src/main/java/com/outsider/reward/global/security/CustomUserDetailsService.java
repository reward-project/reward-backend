package com.outsider.reward.global.security;

import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.domain.member.exception.MemberErrorCode;
import com.outsider.reward.domain.member.exception.MemberException;
import com.outsider.reward.global.common.exception.BusinessException;
import com.outsider.reward.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("=== CustomUserDetailsService.loadUserByUsername ===");
        log.info("Attempting to load user with email: {}", email);
        
        try {
            Member member = memberRepository.findByBasicInfo_Email(email)
                    .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
            
            CustomUserDetails userDetails = new CustomUserDetails(member);
            log.info("Successfully loaded user: {}", userDetails.getUsername());
            log.info("User authorities: {}", userDetails.getAuthorities());
            return userDetails;
            
        } catch (Exception e) {
            log.error("Failed to load user by email: {}", email, e);
            throw e;
        }
    }
} 