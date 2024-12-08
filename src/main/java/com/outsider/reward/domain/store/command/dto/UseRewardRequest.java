package com.outsider.reward.domain.store.command.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UseRewardRequest {
    private Long storeMissionId;
    private Long userId;
    private double amount;
} 