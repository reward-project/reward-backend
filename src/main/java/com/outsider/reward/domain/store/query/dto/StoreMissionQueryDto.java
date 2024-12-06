package com.outsider.reward.domain.store.query.dto;

import com.outsider.reward.domain.store.command.domain.Platform;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class StoreMissionQueryDto {
    private Long id;
    private String rewardName;
    private Platform platform;
    private String storeName;
    private String registrantName;
    private String productLink;
    private String keyword;
    private String productId;
    private String optionId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String registrantId;
    private String rewardId;
    private Double rewardAmount;
    private Integer maxRewardsPerDay;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
