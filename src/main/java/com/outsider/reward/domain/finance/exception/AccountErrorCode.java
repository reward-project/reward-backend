package com.outsider.reward.domain.finance.exception;

import com.outsider.reward.global.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AccountErrorCode implements ErrorCode {
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACC001", "error.account.not.found"),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "ACC002", "error.account.insufficient.balance"),
    INVALID_TRANSACTION(HttpStatus.BAD_REQUEST, "ACC003", "error.account.invalid.transaction");

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