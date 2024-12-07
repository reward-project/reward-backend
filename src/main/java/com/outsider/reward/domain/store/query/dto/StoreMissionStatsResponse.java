package com.outsider.reward.domain.store.query.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class StoreMissionStatsResponse {
    private final int totalMissions;
    private final int activeMissions;
    private final int completedMissions;
    private final double successRate;
    private final double totalRewardAmount;
    private final double averageRewardAmount;
    private final Map<String, Integer> missionsByPlatform;
    private final List<DailyRewardStats> dailyStats;
    private final int totalUsageCount;
    private final int todayUsageCount;
    private final double usageRate;
    private final Map<Integer, Integer> usageByHour;
    private final Map<String, Integer> usageByDay;
    private final List<UsageStats> recentUsage;

    @Getter
    @Builder
    public static class DailyRewardStats {
        private final LocalDateTime date;
        private final int rewardCount;
        private final double rewardAmount;
    }

    @Getter
    @Builder
    public static class UsageStats {
        private final LocalDateTime timestamp;
        private final String platform;
        private final String storeName;
        private final double amount;
        private final String status;
    }
} 