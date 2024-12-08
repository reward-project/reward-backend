package com.outsider.reward.domain.finance.command.domain;

public enum TransactionType {
    REWARD,         // 리워드 적립
    SETTLEMENT,     // 정산
    REFUND,         // 환불
    WITHDRAWAL,     // 출금
    ADJUSTMENT,     // 조정
    REWARD_BUDGET,  // 리워드 예산 설정
    REWARD_REFUND,  // 리워드 예산 환불
    VIRTUAL_CHARGE, // 가상 충전 (테스트용)
    ADMIN_CHARGE   // 관리자 충전
} 