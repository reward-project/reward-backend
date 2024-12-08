package com.outsider.reward.domain.finance.presentation;

import com.outsider.reward.domain.finance.command.application.AccountService;
import com.outsider.reward.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<Double>> getBalance(
        @RequestAttribute("userId") Long userId
    ) {
        double balance = accountService.getBalance(userId);
        return ResponseEntity.ok(ApiResponse.success(balance));
    }

    @PostMapping("/virtual-charge")
    @PreAuthorize("hasRole('ADMIN')")  // 관리자만 가상 충전 가능
    public ResponseEntity<ApiResponse<Void>> virtualCharge(
        @RequestParam Long userId,
        @RequestParam double amount
    ) {
        accountService.virtualCharge(userId, amount);
        return ResponseEntity.ok(ApiResponse.success(null, "충전이 완료되었습니다."));
    }

    @PostMapping("/admin-charge")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> adminCharge(
        @RequestParam Long userId,
        @RequestParam double amount,
        @RequestParam String reason
    ) {
        accountService.adminCharge(userId, amount, reason);
        return ResponseEntity.ok(ApiResponse.success(null, "충전이 완료되었습니다."));
    }
} 