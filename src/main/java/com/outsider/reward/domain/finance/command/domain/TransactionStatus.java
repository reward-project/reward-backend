package com.outsider.reward.domain.finance.command.domain;

public enum TransactionStatus {
    PENDING,    // 처리 중
    COMPLETED,  // 완료
    FAILED,     // 실패
    CANCELLED   // 취소됨
} 