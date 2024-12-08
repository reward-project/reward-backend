package com.outsider.reward.domain.finance.command.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfo {
    private Long userId;
    private double amount;
    private String orderId;
} 