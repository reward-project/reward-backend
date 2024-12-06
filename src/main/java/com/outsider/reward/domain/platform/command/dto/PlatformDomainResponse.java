package com.outsider.reward.domain.platform.command.dto;

import com.outsider.reward.domain.platform.command.domain.PlatformDomain;
import com.outsider.reward.domain.platform.command.domain.PlatformDomainStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlatformDomainResponse {
    private String domain;
    private PlatformDomainStatus status;

    public static PlatformDomainResponse from(PlatformDomain platformDomain) {
        return PlatformDomainResponse.builder()
                .domain(platformDomain.getDomain())
                .status(platformDomain.getStatus())
                .build();
    }
}
