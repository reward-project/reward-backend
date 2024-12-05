package com.outsider.reward.domain.store.query;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.outsider.reward.domain.store.command.domain.Platform;

@Getter
@Builder
public class StoreMissionQuery {
    private final Long id;
    private final String rewardName;
    private final Platform platform;
    private final String storeName;
    private final String registrantName;
    private final String productLink;
    private final String keyword;
    private final String productId;
    private final String optionId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String registrantId;
    private final String rewardId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
