package com.outsider.reward.domain.finance.command.application;

import com.outsider.reward.domain.finance.command.dto.PaymentRequest;
import com.outsider.reward.domain.finance.command.dto.PaymentReadyResponse;

public interface PaymentService {
    PaymentReadyResponse requestPayment(PaymentRequest request);
    void processPayment(String paymentKey, String orderId);
    void cancelPayment(String paymentKey, String reason);
} 