package com.outsider.reward.domain.member.command.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
public class MemberBasicInfo {
    private String name;
    private String email;
    private String password;
    private String nickname;
    private String profileImageUrl;

    public MemberBasicInfo(String name, String email, String password, String nickname) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    protected void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    protected void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    protected void updateProfileImage(String imageUrl) {
        this.profileImageUrl = imageUrl;
    }
} 