package com.outsider.reward.domain.platform.command.dto;

import com.outsider.reward.domain.platform.command.domain.Platform;
import com.outsider.reward.domain.platform.command.domain.PlatformStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlatformResponse {
    private Long id;
    private String name;
    private String displayName;
    private PlatformStatus status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PlatformResponse from(Platform platform) {
        return PlatformResponse.builder()
                .id(platform.getId())
                .name(platform.getName())
                .displayName(platform.getDisplayName())
                .status(platform.getStatus())
                .description(platform.getDescription())
                .createdAt(platform.getCreatedAt())
                .updatedAt(platform.getUpdatedAt())
                .build();
    }
}
