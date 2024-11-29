package com.outsider.reward.domain.member.query.dto;

import lombok.Getter;
import lombok.Setter;

public class MemberQuery {
    
    @Getter @Setter
    public static class MemberInfo {
        private Long id;
        private String name;
        private String email;
        private String nickname;
        private boolean emailVerified;
    }
} 