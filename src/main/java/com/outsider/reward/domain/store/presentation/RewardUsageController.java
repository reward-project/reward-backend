package com.outsider.reward.domain.store.presentation;

import com.outsider.reward.domain.store.command.dto.UseRewardRequest;
import com.outsider.reward.global.common.response.ApiResponse;
import com.outsider.reward.domain.store.command.application.RewardUsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rewards")
@RequiredArgsConstructor
public class RewardUsageController {
    private final RewardUsageService rewardUsageService;

    @PostMapping("/use")
    public ResponseEntity<ApiResponse<Void>> useReward(@RequestBody UseRewardRequest request) {
        rewardUsageService.useReward(request);
        return ResponseEntity.ok(ApiResponse.success(null, "리워드가 성공적으로 사용되었습니다."));
    }
} 