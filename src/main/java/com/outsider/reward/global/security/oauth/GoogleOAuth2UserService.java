package com.outsider.reward.global.security.oauth;

import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.domain.member.command.domain.OAuthProvider;
import com.outsider.reward.domain.member.command.domain.RoleType;

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
        String sub = oauth2User.getAttribute("sub");
        
        String platform = userRequest.getAdditionalParameters().get("platform").toString();
        String role = userRequest.getAdditionalParameters().get("role").toString();
        RoleType roleType = determineRoleType(role);
        
        Member member = memberRepository.findByBasicInfo_Email(email)
                .orElseGet(() -> createOAuthMember(
                    email,
                    name,
                    "google",
                    sub,
                    roleType
                ));
                
        return new CustomOAuth2User(member, oauth2User.getAttributes());
    }

    private Member createOAuthMember(String email, String name, String provider, String providerId, RoleType roleType) {
        Member member = Member.createMember(
            email,
            name,
            name,  // nickname
            ""     // password (OAuth 사용자는 빈 비밀번호 사용)
        );
        
        OAuthProvider oAuthProvider = OAuthProvider.builder()
            .member(member)
            .provider(provider)
            .providerId(providerId)
            .build();
        
        member.addOAuthProvider(oAuthProvider);
        member.verifyEmail();
        member.addRole(roleType);
        
        return memberRepository.save(member);
    }

    private RoleType determineRoleType(String role) {
        switch (role) {
            case "business":
                return RoleType.ROLE_BUSINESS;
            case "admin":
                return RoleType.ROLE_ADMIN;
            case "user":
            default:
                return RoleType.ROLE_USER;
        }
    }
} 