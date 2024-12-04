package com.outsider.reward.global.security.oauth.service;

import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.global.security.oauth.info.KakaoOAuth2UserInfo;
import com.outsider.reward.global.security.oauth.info.OAuth2UserInfo;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class KakaoOAuth2UserService extends OAuth2UserService {

    public KakaoOAuth2UserService(MemberRepository memberRepository) {
        super(memberRepository);
    }

    @Override
    protected OAuth2UserInfo getOAuth2UserInfo(OAuth2User oAuth2User) {
        return new KakaoOAuth2UserInfo(oAuth2User.getAttributes());
    }
} 