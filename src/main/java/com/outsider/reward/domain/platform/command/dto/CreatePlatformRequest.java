package com.outsider.reward.domain.platform.command.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CreatePlatformRequest {
    @NotBlank(message = "플랫폼 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "표시 이름은 필수입니다.")
    private String displayName;

    @NotEmpty(message = "도메인은 최소 1개 이상 필요합니다.")
    private List<@NotBlank(message = "도메인은 빈 값일 수 없습니다.") String> domains;

    private String description;
}
