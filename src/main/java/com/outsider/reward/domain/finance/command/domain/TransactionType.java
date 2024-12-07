package com.outsider.reward.domain.finance.command.domain;

public enum TransactionType {
    REWARD,         // 리워드 적립
    SETTLEMENT,     // 정산
    REFUND,         // 환불
    WITHDRAWAL,     // 출금
    ADJUSTMENT      // 조정
} 