package com.outsider.reward.domain.finance.command.domain;

public enum SettlementStatus {
    PENDING,    // 정산 대기
    PROCESSING, // 정산 중
    COMPLETED,  // 정산 완료
    FAILED,     // 정산 실패
    CANCELLED   // 정산 취소
} 