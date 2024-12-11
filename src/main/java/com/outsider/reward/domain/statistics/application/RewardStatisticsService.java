package com.outsider.reward.domain.statistics.application;

import com.outsider.reward.domain.store.command.domain.RewardUsageRepository;
import com.outsider.reward.domain.store.command.domain.RewardUsageStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RewardStatisticsService {
    private final RewardUsageRepository rewardUsageRepository;

    @Transactional(readOnly = true)
    public RewardStatisticsDto getRewardStatistics() {
        long totalRewards = rewardUsageRepository.count();
        long pendingRewards = rewardUsageRepository.countByStatus(RewardUsageStatus.PENDING);
        long completedRewards = rewardUsageRepository.countByStatus(RewardUsageStatus.COMPLETED);
        long failedRewards = rewardUsageRepository.countByStatus(RewardUsageStatus.FAILED);
        
        return RewardStatisticsDto.builder()
                .totalRewards(totalRewards)
                .activeRewards(pendingRewards)  // PENDING rewards are considered active
                .pendingRewards(pendingRewards)
                .completedRewards(completedRewards)
                .build();
    }
}
