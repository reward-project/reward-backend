package com.outsider.reward.domain.finance.presentation;

import com.outsider.reward.domain.finance.command.application.RewardSettlementService;
import com.outsider.reward.global.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/settlements")
public class SettlementController {
    private final RewardSettlementService settlementService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> requestSettlement(
        @RequestAttribute("userId") Long userId,
        @RequestParam double amount
    ) {
        settlementService.settleReward(userId, amount);
        return ResponseEntity.ok(ApiResponse.success(null, "정산이 요청되었습니다."));
    }
} 