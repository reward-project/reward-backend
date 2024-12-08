package com.outsider.reward.domain.finance.exception;

import com.outsider.reward.global.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AccountErrorCode implements ErrorCode {
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACC001"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "ACC002"),
    BANK_INFO_NOT_FOUND(HttpStatus.BAD_REQUEST, "ACC003"),
    BANK_INFO_INVALID(HttpStatus.BAD_REQUEST, "ACC004"),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "ACC005"),
    TRANSFER_FAILED(HttpStatus.BAD_REQUEST, "ACC006");

    private final HttpStatus httpStatus;
    private final String code;

    @Override
    public int getStatus() {
        return this.httpStatus.value();
    }

    @Override
    public String getMessageKey() {
        return "error." + this.code;
    }

    @Override
    public String getCode() {
        return this.code;
    }
} 