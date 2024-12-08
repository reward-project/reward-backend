package com.outsider.reward.domain.finance.presentation;

import com.outsider.reward.domain.finance.command.application.PaymentService;
import com.outsider.reward.domain.finance.command.dto.PaymentReadyResponse;
import com.outsider.reward.domain.finance.command.dto.PaymentRequest;
import com.outsider.reward.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/ready")
    public ResponseEntity<ApiResponse<PaymentReadyResponse>> requestPayment(
        @RequestBody PaymentRequest request
    ) {
        PaymentReadyResponse response = paymentService.requestPayment(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/success")
    public ResponseEntity<ApiResponse<Void>> paymentSuccess(
        @RequestParam("payment_key") String paymentKey,
        @RequestParam("order_id") String orderId
    ) {
        paymentService.processPayment(paymentKey, orderId);
        return ResponseEntity.ok(ApiResponse.success(null, "결제가 완료되었습니다."));
    }
} 