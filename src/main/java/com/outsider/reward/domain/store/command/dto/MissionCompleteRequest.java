package com.outsider.reward.domain.store.command.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MissionCompleteRequest {
    @Setter
    private Long userId;
    private String missionAnswer;
}
