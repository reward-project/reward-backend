package com.outsider.reward.domain.finance.presentation;

import com.outsider.reward.domain.finance.command.application.NaverPayService;
import com.outsider.reward.domain.finance.command.dto.PaymentReadyResponse;
import com.outsider.reward.domain.finance.command.dto.PaymentRequest;
import com.outsider.reward.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments/naver")
public class NaverPayController {
    private final NaverPayService naverPayService;
    private static final Logger log = LoggerFactory.getLogger(NaverPayController.class);

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<PaymentReadyResponse>> requestPayment(
        @RequestBody PaymentRequest request
    ) {
        PaymentReadyResponse response = naverPayService.requestPayment(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/success")
    public ResponseEntity<ApiResponse<Void>> paymentSuccess(
        @RequestParam("payment_key") String paymentKey,
        @RequestParam("order_id") String orderId
    ) {
        naverPayService.processPayment(paymentKey, orderId);
        return ResponseEntity.ok(ApiResponse.success(null, "결제가 완료되었습니다."));
    }

    @GetMapping("/fail")
    public ResponseEntity<ApiResponse<Void>> paymentFail(
        @RequestParam("payment_key") String paymentKey,
        @RequestParam("message") String message
    ) {
        naverPayService.cancelPayment(paymentKey, message);
        return ResponseEntity.ok(ApiResponse.success(null, "결제가 취소되었습니다."));
    }

    @GetMapping("/cancel")
    public ResponseEntity<ApiResponse<Void>> paymentCancel(
        @RequestParam("payment_key") String paymentKey
    ) {
        naverPayService.cancelPayment(paymentKey, "사용자 취소");
        return ResponseEntity.ok(ApiResponse.success(null, "결제가 취소되었습니다."));
    }

    @GetMapping("/callback")
    public ResponseEntity<ApiResponse<Void>> paymentCallback(
        @RequestParam("paymentId") String paymentId,
        @RequestParam("merchantPayKey") String orderId,
        @RequestParam("resultCode") String resultCode,
        @RequestParam(value = "resultMessage", required = false) String resultMessage
    ) {
        log.debug("네이버페이 콜백: paymentId={}, orderId={}, resultCode={}, message={}", 
            paymentId, orderId, resultCode, resultMessage);

        if ("Success".equals(resultCode)) {
            naverPayService.processPayment(paymentId, orderId);
            return ResponseEntity.ok(ApiResponse.success(null, "결제가 완료되었습니다."));
        } else {
            naverPayService.cancelPayment(paymentId, resultMessage);
            return ResponseEntity.ok(ApiResponse.success(null, "결제가 취소되었습니다."));
        }
    }
} 