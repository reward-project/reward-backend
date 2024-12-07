package com.outsider.reward.domain.store.query.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@Getter
@SuperBuilder
public class RewardInfo {
    private final String rewardId;
    private final String rewardName;
    private final Double rewardAmount;
    private final Integer maxRewardsPerDay;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String status;  // 리워드 상태 추가
} 