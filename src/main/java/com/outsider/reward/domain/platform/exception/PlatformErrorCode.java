package com.outsider.reward.domain.platform.exception;

import com.outsider.reward.global.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PlatformErrorCode implements ErrorCode {
    PLATFORM_NOT_FOUND(HttpStatus.NOT_FOUND, "PF001", "error.platform.not.found"),
    DUPLICATE_PLATFORM(HttpStatus.BAD_REQUEST, "PF002", "error.platform.duplicate"),
    DUPLICATE_PLATFORM_DOMAIN(HttpStatus.BAD_REQUEST, "PF003", "error.platform.domain.duplicate"),
    INVALID_PLATFORM_STATUS(HttpStatus.BAD_REQUEST, "PF004", "error.platform.status.invalid"),
    INVALID_PLATFORM_DOMAIN(HttpStatus.BAD_REQUEST, "PF005", "error.platform.domain.invalid");

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
