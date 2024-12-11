package com.outsider.reward.domain.statistics.application;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RewardStatisticsDto {
    private final long totalRewards;
    private final long activeRewards;
    private final long pendingRewards;
    private final long completedRewards;
}
