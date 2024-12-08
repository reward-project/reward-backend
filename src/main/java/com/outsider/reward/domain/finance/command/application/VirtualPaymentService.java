package com.outsider.reward.domain.finance.command.application;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outsider.reward.domain.finance.command.domain.TransactionType;
import com.outsider.reward.domain.finance.command.dto.PaymentInfo;
import com.outsider.reward.domain.finance.command.dto.PaymentReadyResponse;
import com.outsider.reward.domain.finance.command.dto.PaymentRequest;
import com.outsider.reward.domain.finance.exception.PaymentErrorCode;
import com.outsider.reward.domain.finance.exception.PaymentException;

import java.util.UUID;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Profile({"local", "dev"})  // 개발 환경에서만 사용
public class VirtualPaymentService implements PaymentService {
    private final AccountService accountService;
    private final Map<String, PaymentInfo> paymentInfoMap = new ConcurrentHashMap<>();

    @Override
    public PaymentReadyResponse requestPayment(PaymentRequest request) {
        String paymentKey = UUID.randomUUID().toString();
        paymentInfoMap.put(request.getOrderId(), PaymentInfo.builder()
            .userId(request.getUserId())
            .amount(request.getAmount())
            .orderId(request.getOrderId())
            .build());
            
        return PaymentReadyResponse.builder()
            .paymentKey(paymentKey)
            .orderId(request.getOrderId())
            .successUrl("/api/v1/payments/success")
            .build();
    }

    @Override
    @Transactional
    public void processPayment(String paymentKey, String orderId) {
        PaymentInfo paymentInfo = getPaymentInfo(orderId);
        accountService.addBalance(
            paymentInfo.getUserId(),
            paymentInfo.getAmount(),
            TransactionType.VIRTUAL_CHARGE,
            "가상 결제: " + orderId
        );
    }

    @Override
    public void cancelPayment(String paymentKey, String reason) {
        // 개발용이므로 실제 구현 없음
    }

    private PaymentInfo getPaymentInfo(String orderId) {
        return Optional.ofNullable(paymentInfoMap.get(orderId))
            .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));
    }
} 