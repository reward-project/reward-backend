package com.outsider.reward.domain.store.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import com.outsider.reward.global.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum StoreMissionErrorCode implements ErrorCode {

    STORE_MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "SM001", "error.store.mission.not.found"),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "SM002", "error.store.mission.date.range.invalid"),
    PAST_START_DATE(HttpStatus.BAD_REQUEST, "SM003", "error.store.mission.date.past"),
    INVALID_PRODUCT_LINK(HttpStatus.BAD_REQUEST, "SM004", "error.store.mission.product.link.invalid"),
    INVALID_PLATFORM(HttpStatus.BAD_REQUEST, "SM005", "error.store.mission.platform.invalid"),
    DUPLICATE_STORE_MISSION(HttpStatus.CONFLICT, "SM006", "error.store.mission.duplicate"),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "SM007", "error.store.mission.unauthorized");

    private final HttpStatus httpStatus;
    private final String code;
    private final String messageKey;

    @Override
    public int getStatus() {
        return this.httpStatus.value();
    }

    @Override
    public String getMessageKey() {
        return this.messageKey;
    }

    @Override
    public String getCode() {
        return this.code;
    }
}
