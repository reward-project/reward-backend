package com.outsider.reward.domain.platform.command.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddPlatformDomainRequest {
    @NotBlank(message = "도메인은 필수입니다")
    private String domain;
    
    private String description;
}
