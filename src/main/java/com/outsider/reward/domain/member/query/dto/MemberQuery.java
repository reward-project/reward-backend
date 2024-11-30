package com.outsider.reward.domain.member.query.dto;

import lombok.Getter;

public class MemberQuery {

    @Getter
    public static class MemberInfo {
        private final Long id;
        private final String name;
        private final String email;
        private final String nickname;
        private final String profileImageUrl;

        public MemberInfo(Long id, String name, String email, String nickname, String profileImageUrl) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.nickname = nickname;
            this.profileImageUrl = profileImageUrl;
        }
    }
} 