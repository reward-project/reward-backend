package com.outsider.reward.domain.member.command.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class MemberCommand {
    
    @Getter @Setter
    public static class SignUp {
        @NotBlank(message = "{member.name.required}")
        @Size(min = 2, max = 20, message = "{member.name.size}")
        private String name;

        @NotBlank(message = "{member.email.required}")
        @Email(message = "{member.email.invalid}")
        private String email;

        @NotBlank(message = "{member.password.required}")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "{member.password.pattern}")
        private String password;

        @NotBlank(message = "{member.nickname.required}")
        @Size(min = 2, max = 20, message = "{member.nickname.size}")
        private String nickname;
    }
    
    @Getter @Setter
    @ToString
    public static class Login {
        @NotBlank(message = "{member.email.login.required}")
        private String email;
        
        @NotBlank(message = "{member.password.login.required}")
        private String password;

        @NotBlank(message = "{member.role.required}")
        private String role;
    }
    
    @Getter
    public static class LoginResponse {
        private final String accessToken;
        private final String refreshToken;
        
        public LoginResponse(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
    
    @Getter @Setter
    public static class UpdateProfile {
        @Size(min = 2, max = 20, message = "{member.nickname.size}")
        private String nickname;
        
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "{member.password.pattern}")
        private String newPassword;
        
        private String currentPassword;
    }
} 