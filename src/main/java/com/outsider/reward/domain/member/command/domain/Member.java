package com.outsider.reward.domain.member.command.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    private boolean emailVerified;
    private String provider;    // OAuth2 제공자 (google, kakao 등)
    private String providerId;  // OAuth2 제공자의 식별자

    // Add this field and getter if you need creation time
    private LocalDateTime createdAt;

    @Builder
    public Member(String email, String password, String name, String nickname) {
        this.basicInfo = new MemberBasicInfo(name, email, password, nickname);
        this.emailVerified = false;
    }

    public void verifyEmail() {
        this.emailVerified = true;
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

    public void setOAuthInfo(String provider, String providerId) {
        this.provider = provider;
        this.providerId = providerId;
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
} 