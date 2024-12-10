package com.outsider.reward.domain.store.command.domain;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class StoreMissionWithCompletion {
    private final StoreMission mission;
    private final boolean completed;

    @QueryProjection
    public StoreMissionWithCompletion(StoreMission mission, boolean completed) {
        this.mission = mission;
        this.completed = completed;
    }
}
