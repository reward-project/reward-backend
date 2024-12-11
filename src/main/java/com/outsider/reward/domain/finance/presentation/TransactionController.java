package com.outsider.reward.domain.finance.presentation;

import com.outsider.reward.domain.finance.query.application.TransactionQueryService;
import com.outsider.reward.domain.finance.query.dto.TransactionListResponse;
import com.outsider.reward.global.common.response.ApiResponse;
import com.outsider.reward.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/finance/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionQueryService transactionQueryService;

    @GetMapping
    public ResponseEntity<ApiResponse<TransactionListResponse>> getTransactions(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        TransactionListResponse response = transactionQueryService.getTransactions(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
