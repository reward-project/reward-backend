package com.outsider.reward.domain.statistics.presentation;

import com.outsider.reward.domain.statistics.application.RewardStatisticsDto;
import com.outsider.reward.domain.statistics.application.RewardStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class RewardStatisticsController {
    private final RewardStatisticsService rewardStatisticsService;

    @GetMapping("/rewards")
    public ResponseEntity<RewardStatisticsDto> getRewardStatistics() {
        return ResponseEntity.ok(rewardStatisticsService.getRewardStatistics());
    }
}
