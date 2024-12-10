package com.outsider.reward.domain.store.command.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MissionCompleteResponse {
    private Long missionId;
    private double rewardPoint;
    private String status;
}
