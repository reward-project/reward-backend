package com.outsider.reward.global.security.oauth;

import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
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
        
        Member member = memberRepository.findByBasicInfo_Email(email)
                .orElseGet(() -> createMember(email, name));
                
        return new CustomOAuth2User(member, oauth2User.getAttributes());
    }

    private Member createMember(String email, String name) {
        Member member = Member.builder()
                .email(email)
                .name(name)
                .nickname(name)
                .password("")
                .build();
        
        member.setOAuthInfo("google", email);
        member.setEmailVerified(true);
        
        return memberRepository.save(member);
    }
} 