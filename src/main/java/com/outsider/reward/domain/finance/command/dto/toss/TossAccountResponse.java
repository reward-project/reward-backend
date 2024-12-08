package com.outsider.reward.domain.finance.command.dto.toss;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossAccountResponse {
    private String bank;           // 은행코드
    private String accountNumber;  // 계좌번호
    private String holderName;     // 예금주명
    private boolean success;       // 성공여부
    private String message;        // 응답메시지
} 