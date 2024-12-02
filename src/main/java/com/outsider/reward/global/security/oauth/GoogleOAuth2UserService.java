package com.outsider.reward.global.security.oauth;

import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.domain.member.command.domain.OAuthProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String sub = oauth2User.getAttribute("sub");  // Google의 고유 식별자
        
        Member member = memberRepository.findByBasicInfo_Email(email)
                .orElseGet(() -> createMember(email, name, sub));
                
        return new CustomOAuth2User(member, oauth2User.getAttributes());
    }

    private Member createMember(String email, String name, String sub) {
        Member member = Member.createMember(
            email,
            name,
            name,  // nickname
            ""     // password
        );
        
        OAuthProvider oAuthProvider = OAuthProvider.builder()
            .member(member)
            .provider("google")
            .providerId(sub)
            .build();
        
        member.addOAuthProvider(oAuthProvider);
        member.verifyEmail();
        
        return memberRepository.save(member);
    }
} 