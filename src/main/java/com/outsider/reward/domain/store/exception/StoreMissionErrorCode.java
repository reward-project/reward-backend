package com.outsider.reward.domain.store.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import com.outsider.reward.global.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum StoreMissionErrorCode implements ErrorCode {

    STORE_MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "SM001", "Store mission not found"),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "SM002", "Invalid date range. End date must be after start date"),
    PAST_START_DATE(HttpStatus.BAD_REQUEST, "SM003", "Start date cannot be in the past"),
    INVALID_PRODUCT_LINK(HttpStatus.BAD_REQUEST, "SM004", "Invalid product link format"),
    INVALID_PLATFORM(HttpStatus.BAD_REQUEST, "SM005", "Invalid platform type"),
    DUPLICATE_STORE_MISSION(HttpStatus.CONFLICT, "SM006", "Duplicate store mission exists"),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "SM007", "Unauthorized access to store mission");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public int getStatus() {
        return this.httpStatus.value();
    }

    @Override
    public String getMessageKey() {
        return this.message;
    }
}
