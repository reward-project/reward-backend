package com.outsider.reward.global.security.oauth.service;

import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.global.security.oauth.info.GoogleOAuth2UserInfo;
import com.outsider.reward.global.security.oauth.info.OAuth2UserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class GoogleOAuth2UserService extends OAuth2UserService {
    
    public GoogleOAuth2UserService(MemberRepository memberRepository) {
        super(memberRepository);
    }

    @Override
    protected OAuth2UserInfo getOAuth2UserInfo(OAuth2User oauth2User) {
        return new GoogleOAuth2UserInfo(oauth2User.getAttributes());
    }
} 