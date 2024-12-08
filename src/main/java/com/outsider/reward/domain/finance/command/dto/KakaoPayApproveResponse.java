package com.outsider.reward.domain.finance.command.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayApproveResponse {
    private String aid;
    private String tid;
    private String cid;
    private String partnerOrderId;
    private String partnerUserId;
    private PaymentAmount amount;
    private String orderId;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentAmount {
        private int total;
        private int taxFree;
        private int vat;
        private int point;
        private int discount;
    }
} 