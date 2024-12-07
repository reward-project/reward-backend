package com.outsider.reward.domain.platform.command.dto;

import com.outsider.reward.domain.platform.command.domain.Platform;
import com.outsider.reward.domain.platform.command.domain.PlatformDomain;
import com.outsider.reward.domain.platform.command.domain.PlatformStatus;
import lombok.Builder;
import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlatformResponse {
    private final Long id;
    private final String name;
    private final String displayName;
    private final String status;
    private final List<String> domains;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static PlatformResponse from(Platform platform) {
        return PlatformResponse.builder()
                .id(platform.getId())
                .name(platform.getName())
                .displayName(platform.getDisplayName())
                .status(platform.getStatus().toString())
                .domains(platform.getDomains().stream()
                        .map(PlatformDomain::getDomain)
                        .toList())
                .createdAt(platform.getCreatedAt())
                .updatedAt(platform.getUpdatedAt())
                .build();
    }
}
