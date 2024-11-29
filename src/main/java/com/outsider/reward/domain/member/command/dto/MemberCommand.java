package com.outsider.reward.domain.member.command.dto;

import lombok.Getter;
import lombok.Setter;

public class MemberCommand {
    
    @Getter @Setter
    public static class SignUp {
        private String name;
        private String email;
        private String password;
        private String nickname;
    }
    
    @Getter @Setter
    public static class Login {
        private String email;
        private String password;
    }
    
    @Getter
    public static class LoginResponse {
        private final Long memberId;
        
        public LoginResponse(Long memberId) {
            this.memberId = memberId;
        }
    }
} 