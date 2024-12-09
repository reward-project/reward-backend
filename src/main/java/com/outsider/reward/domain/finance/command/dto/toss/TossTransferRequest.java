package com.outsider.reward.domain.finance.command.dto.toss;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TossTransferRequest {
    private String bank;           // 은행코드
    private String accountNumber;  // 계좌번호
    private Long amount;           // 이체금액
    private String holderName;     // 예금주
    private String purpose;        // 이체용도(리워드정산)
    private String orderId;        // 주문번호
} 