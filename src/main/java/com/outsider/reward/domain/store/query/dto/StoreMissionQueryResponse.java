package com.outsider.reward.domain.store.query.dto;

import com.outsider.reward.domain.store.command.domain.StoreMission;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class StoreMissionQueryResponse {
    private Long id;
    private String title;
    private String description;
    private int rewardPoint;
    private String status;
    private String missionUrl;
    private boolean completed;
    
    public static StoreMissionQueryResponse of(Long id, String title, String description, int rewardPoint, String status, String missionUrl, boolean completed) {
        return StoreMissionQueryResponse.builder()
            .id(id)
            .title(title)
            .description(description)
            .rewardPoint(rewardPoint)
            .status(status)
            .missionUrl(missionUrl)
            .completed(completed)
            .build();
    }
}
