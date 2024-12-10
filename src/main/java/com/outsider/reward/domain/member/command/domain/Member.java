package com.outsider.reward.domain.member.command.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private MemberBasicInfo basicInfo;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OAuthProvider> oAuthProviders = new ArrayList<>();

    private boolean emailVerified;
    private LocalDateTime createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = "member_roles",
        joinColumns = @JoinColumn(name = "member_id")
    )
    @Column(name = "role")
    private Set<RoleType> roles = new HashSet<>();

    public static Member createMember(String email, String name, String nickname, String password) {
        Member member = new Member();
        member.basicInfo = new MemberBasicInfo(email, name, nickname, password);
        member.emailVerified = false;
        member.createdAt = LocalDateTime.now();
        member.oAuthProviders = new ArrayList<>();
        member.roles = new HashSet<>();
        return member;
    }

    public static Member createOAuthMember(String email, String name, String nickname, String provider) {
        Member member = new Member();
        member.basicInfo = new MemberBasicInfo(email, name, nickname, null);  // OAuth 사용자는 password 불필요
        member.emailVerified = true;  // OAuth로 가입한 사용자는 이메일이 이미 인증됨
        member.createdAt = LocalDateTime.now();
        member.oAuthProviders = new ArrayList<>();
        member.roles = new HashSet<>();

        // OAuth provider 추가
        OAuthProvider oAuthProvider = OAuthProvider.builder()
            .member(member)
            .provider(provider)
            .providerId(email)  // providerId를 email로 사용
            .build();
        member.addOAuthProvider(oAuthProvider);

        return member;
    }

    public void addOAuthProvider(OAuthProvider provider) {
        this.oAuthProviders.add(provider);
    }

    public void verifyEmail() {
        this.emailVerified = true;
    }

    public boolean hasProvider(String provider) {
        return this.oAuthProviders.stream()
            .anyMatch(p -> p.getProvider().equals(provider));
    }

    public Optional<OAuthProvider> getProvider(String provider) {
        return this.oAuthProviders.stream()
            .filter(p -> p.getProvider().equals(provider))
            .findFirst();
    }

    public void updatePassword(String newPassword) {
        this.basicInfo.updatePassword(newPassword);
    }

    public void updateNickname(String nickname) {
        this.basicInfo.updateNickname(nickname);
    }

    public void updateProfileImage(String imageUrl) {
        this.basicInfo.updateProfileImage(imageUrl);
    }

    public void setEmailVerified(boolean verified) {
        this.emailVerified = verified;
    }

    public String getEmail() {
        return basicInfo.getEmail();
    }

    public String getPassword() {
        return basicInfo.getPassword();
    }

    public String getName() {
        return basicInfo.getName();
    }

    public String getNickname() {
        return basicInfo.getNickname();
    }

    public String getProfileImageUrl() {
        return basicInfo.getProfileImageUrl();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void addRole(RoleType roleType) {
        this.roles.add(roleType);
    }

    public void removeRole(RoleType roleType) {
        this.roles.remove(roleType);
    }

    public boolean hasRole(RoleType roleType) {
        return this.roles.contains(roleType);
    }

    public Set<String> getRoleNames() {
        return this.roles.stream()
            .map(RoleType::name)
            .collect(Collectors.toSet());
    }
} 