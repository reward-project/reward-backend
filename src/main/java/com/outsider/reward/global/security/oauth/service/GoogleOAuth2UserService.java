package com.outsider.reward.global.security.oauth.service;

import com.outsider.reward.domain.member.command.application.MemberCommandService;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.global.security.oauth.info.GoogleOAuth2UserInfo;
import com.outsider.reward.global.security.oauth.info.OAuth2UserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class GoogleOAuth2UserService extends OAuth2UserService {
    
    public GoogleOAuth2UserService(MemberRepository memberRepository, MemberCommandService memberCommandService) {
        super(memberRepository, memberCommandService);
    }

    @Override
    protected OAuth2UserInfo getOAuth2UserInfo(OAuth2User oAuth2User) {
        return new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
    }
}