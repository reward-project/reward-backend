package com.outsider.reward.domain.store.exception;

import com.outsider.reward.global.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreMissionErrorCode implements ErrorCode {
    // 기존 에러 코드들
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "SM001", "error.store.mission.invalid.date.range"),
    INVALID_BUDGET(HttpStatus.BAD_REQUEST, "SM002", "error.store.mission.invalid.budget"),
    INVALID_REWARD_AMOUNT(HttpStatus.BAD_REQUEST, "SM003", "error.store.mission.invalid.reward.amount"),
    INVALID_MAX_REWARDS(HttpStatus.BAD_REQUEST, "SM004", "error.store.mission.invalid.max.rewards"),
    INVALID_PRODUCT_ID(HttpStatus.BAD_REQUEST, "SM005", "error.store.mission.invalid.product.id"),
    INVALID_MISSION_TYPE(HttpStatus.BAD_REQUEST, "SM006", "error.store.mission.invalid.type"),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "SM007", "error.store.mission.unauthorized"),
    ALREADY_USED_REWARD(HttpStatus.BAD_REQUEST, "SM008", "error.store.mission.reward.already.used"),
    REWARD_USAGE_EXCEEDED(HttpStatus.BAD_REQUEST, "SM009", "error.store.mission.reward.usage.exceeded"),
    MISSION_NOT_EXPIRED(HttpStatus.BAD_REQUEST, "SM010", "error.store.mission.not.expired"),

    // 미션 완료 관련 에러 코드들
    STORE_MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "SM011", "error.store.mission.not.found"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "SM012", "error.store.mission.user.not.found"),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "SM013", "error.store.mission.account.not.found"),
    MISSION_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "SM014", "error.store.mission.already.completed"),
    INVALID_MISSION_ANSWER(HttpStatus.BAD_REQUEST, "SM015", "error.store.mission.wrong.answer"),
    MISSION_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "SM016", "error.store.mission.not.active"),
    BUDGET_EXCEEDED(HttpStatus.BAD_REQUEST, "SM017", "error.store.mission.budget.exceeded"),
    DAILY_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "SM018", "error.store.mission.daily.limit.exceeded"),
    STORE_MISSION_BUDGET_EXCEEDED(HttpStatus.BAD_REQUEST, "SM019", "error.store.mission.budget.or.daily.limit.exceeded"),
    PAST_START_DATE(HttpStatus.BAD_REQUEST, "SM020", "error.store.mission.past.start.date"),
    INVALID_PRODUCT_LINK(HttpStatus.BAD_REQUEST, "SM021", "error.store.mission.invalid.product.link"),
    STORE_MISSION_NOT_FOUND2(HttpStatus.NOT_FOUND, "SM022", "error.store.mission.not.found"),
    MISSION_ALREADY_COMPLETED2(HttpStatus.BAD_REQUEST, "SM023", "error.store.mission.already.completed"),
    INVALID_MISSION_ANSWER2(HttpStatus.BAD_REQUEST, "SM024", "error.store.mission.wrong.answer"),
    STORE_MISSION_BUDGET_EXCEEDED2(HttpStatus.BAD_REQUEST, "SM025", "error.store.mission.budget.or.daily.limit.exceeded");

    private final HttpStatus httpStatus;
    private final String code;
    private final String messageKey;

    @Override
    public int getStatus() {
        return httpStatus.value();
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessageKey() {
        return messageKey;
    }
}
