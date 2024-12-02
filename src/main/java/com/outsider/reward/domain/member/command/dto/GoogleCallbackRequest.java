package com.outsider.reward.domain.member.command.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class GoogleCallbackRequest {
    @NotNull(message = "ID token is required")
    @NotEmpty(message = "ID token cannot be empty")
    private String idToken;
    
    private String role = "user";  // 기본값 설정
} 