package com.outsider.reward.global.security.oauth.service;

import com.outsider.reward.domain.member.command.application.MemberCommandService;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth2UserServiceFactory {

    @Bean
    public GoogleOAuth2UserService googleOAuth2UserService(MemberRepository memberRepository, MemberCommandService memberCommandService) {
        return new GoogleOAuth2UserService(memberRepository, memberCommandService);
    }

    @Bean
    public KakaoOAuth2UserService kakaoOAuth2UserService(MemberRepository memberRepository, MemberCommandService memberCommandService) {
        return new KakaoOAuth2UserService(memberRepository, memberCommandService);
    }
}
