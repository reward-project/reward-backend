package com.outsider.reward.domain.finance.command.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Long userId;
    private String orderId;
    private String itemName;
    private double amount;
} 