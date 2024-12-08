package com.outsider.reward.domain.finance.presentation;

import com.outsider.reward.domain.finance.command.dto.KakaoPayChargeRequest;
import com.outsider.reward.domain.finance.command.application.KakaoPayService;
import com.outsider.reward.domain.finance.command.dto.KakaoPayReadyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments/kakao")
public class KakaoPayController {
    private final KakaoPayService kakaoPayService;

    @PostMapping("/ready")
    public ResponseEntity<KakaoPayReadyResponse> requestPayment(
        @RequestBody KakaoPayChargeRequest request
    ) {
        return ResponseEntity.ok(kakaoPayService.requestPayment(request));
    }

    @GetMapping("/success")
    public ResponseEntity<Void> paymentSuccess(
        @RequestParam("pg_token") String pgToken,
        @RequestParam("order_id") String orderId
    ) {
        kakaoPayService.processPaymentSuccess(pgToken, orderId);
        return ResponseEntity.ok().build();
    }
} 