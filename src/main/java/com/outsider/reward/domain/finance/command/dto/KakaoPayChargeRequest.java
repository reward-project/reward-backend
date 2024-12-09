package com.outsider.reward.domain.finance.command.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoPayChargeRequest {
    private Long userId;
    private double amount;
    private String orderId;    // 주문번호
    private String itemName;   // 상품명 (예: "리워드 예산 충전")
} 