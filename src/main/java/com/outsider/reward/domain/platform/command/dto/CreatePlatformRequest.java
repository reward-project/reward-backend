package com.outsider.reward.domain.platform.command.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePlatformRequest {
    @NotBlank(message = "플랫폼 이름은 필수입니다")
    private String name;

    @NotBlank(message = "표시 이름은 필수입니다")
    private String displayName;

    private String description;
}
