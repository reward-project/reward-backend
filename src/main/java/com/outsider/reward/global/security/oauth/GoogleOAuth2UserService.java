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
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String sub = oauth2User.getAttribute("sub");
        
        Map<String, Object> additionalParameters = userRequest.getAdditionalParameters();
        log.info("OAuth2 User Request - Additional Parameters: {}", additionalParameters);
        
        String platform = additionalParameters.getOrDefault("platform", "web").toString();
        String role = additionalParameters.getOrDefault("role", "user").toString();
        
        log.info("Processing OAuth2 login - Email: {}, Platform: {}, Role: {}", email, platform, role);
        
        RoleType roleType = determineRoleType(role);
        log.debug("Determined RoleType: {}", roleType);
        
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
        return switch (role.toLowerCase()) {
            case "business" -> RoleType.ROLE_BUSINESS;
            case "admin" -> RoleType.ROLE_ADMIN;
            default -> RoleType.ROLE_USER;
        };
    }
} 