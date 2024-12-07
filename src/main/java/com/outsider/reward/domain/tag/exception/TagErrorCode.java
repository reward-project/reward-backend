package com.outsider.reward.domain.tag.exception;

import com.outsider.reward.global.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TagErrorCode implements ErrorCode {
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "tag.not.found"),
    NOT_TAG_OWNER(HttpStatus.FORBIDDEN, "tag.not.owner"),
    ALREADY_SHARED(HttpStatus.BAD_REQUEST, "tag.already.shared"),
    INVALID_PERMISSION(HttpStatus.BAD_REQUEST, "tag.invalid.permission");

    private final HttpStatus status;
    private final String messageKey;

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public int getStatus() {
        return status.value();
    }
} 