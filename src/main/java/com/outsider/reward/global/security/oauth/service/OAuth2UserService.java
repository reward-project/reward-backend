package com.outsider.reward.global.security.oauth.service;

import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.global.security.oauth.CustomOAuth2User;
import com.outsider.reward.global.security.oauth.info.OAuth2UserInfo;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public abstract class OAuth2UserService extends DefaultOAuth2UserService {
    
    protected final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        final String platform = extractCookieValue(request, "platform");
        final String role = extractCookieValue(request, "role");

        if (platform == null || role == null) {
            throw new OAuth2AuthenticationException("Platform or Role cookie is missing");
        }

        log.info("OAuth2 Login - Platform: {}, Role: {}", platform, role);
        
        OAuth2UserInfo userInfo = getOAuth2UserInfo(oauth2User);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        
        Member member = memberRepository.findByBasicInfo_Email(userInfo.getEmail())
            .orElseGet(() -> createOAuthMember(userInfo, provider, platform, role));
            
        return new CustomOAuth2User(member, oauth2User.getAttributes());
    }

    private String extractCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    protected abstract OAuth2UserInfo getOAuth2UserInfo(OAuth2User oauth2User);

    private Member createOAuthMember(OAuth2UserInfo userInfo, String provider, String platform, String role) {
        log.info("Creating new OAuth2 Member - Provider: {}, Platform: {}, Role: {}, Email: {}", 
            provider, platform, role, userInfo.getEmail());
            
        return Member.createMember(
            userInfo.getEmail(),
            userInfo.getName(),
            userInfo.getName(),  // nickname을 name으로 설정
            null  // OAuth 사용자는 password 불필요
        );
    }
} 