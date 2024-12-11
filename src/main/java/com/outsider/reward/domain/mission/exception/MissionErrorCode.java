package com.outsider.reward.domain.mission.exception;

import com.outsider.reward.global.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MissionErrorCode implements ErrorCode {
    MISSION_NOT_FOUND(404, "error.mission.not.found"),
    USER_NOT_FOUND(404, "error.user.not.found"),
    ACCOUNT_NOT_FOUND(404, "error.account.not.found"),
    ALREADY_USED_REWARD(400, "error.mission.reward.already.used"),
    MISSION_EXPIRED(400, "error.mission.expired"),
    MISSION_NOT_ACTIVE(400, "error.mission.not.active"),
    INVALID_MISSION_ANSWER(400, "error.mission.invalid.answer"),
    MISSION_ALREADY_COMPLETED(400, "error.mission.already.completed"),
    INSUFFICIENT_BUDGET(400, "error.mission.insufficient.budget"),
    DAILY_LIMIT_EXCEEDED(400, "error.mission.daily.limit.exceeded");

    private final int status;
    private final String messageKey;

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public String getMessageKey() {
        return this.messageKey;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
