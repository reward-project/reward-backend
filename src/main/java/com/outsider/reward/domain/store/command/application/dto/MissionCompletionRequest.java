package com.outsider.reward.domain.store.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MissionCompletionRequest {
    private Long missionId;
    private String answer;
}
