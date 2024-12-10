package com.outsider.reward.domain.store.command.domain;

public enum MissionStatus {
    SCHEDULED,          // 시작일 이전
    ACTIVE,            // 활성 상태 (참여 가능)
    DAILY_LIMIT_REACHED, // 오늘 최대 리워드 도달
    BUDGET_EXHAUSTED,   // 총 예산 소진
    EXPIRED            // 종료일 이후
}
