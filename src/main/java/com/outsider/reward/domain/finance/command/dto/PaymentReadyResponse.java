package com.outsider.reward.domain.finance.command.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReadyResponse {
    private String paymentKey;
    private String orderId;
    private String successUrl;
    private String failUrl;
    private String cancelUrl;
} 