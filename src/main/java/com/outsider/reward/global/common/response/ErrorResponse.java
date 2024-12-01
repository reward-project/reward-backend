package com.outsider.reward.global.common.response;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import com.outsider.reward.global.common.exception.ErrorCode;
import com.outsider.reward.global.i18n.MessageUtils;
import lombok.Getter;

@Getter
public class ErrorResponse {
    private final int status;
    private final String message;
    private final String code;

    private ErrorResponse(ErrorCode errorCode, MessageUtils messageUtils) {
        this.status = errorCode.getStatus();
        this.message = messageUtils.getMessage(errorCode.getMessageKey());
        this.code = errorCode.getCode();
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode, MessageUtils messageUtils) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ErrorResponse(errorCode, messageUtils));
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode, BindingResult bindingResult, MessageUtils messageUtils) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new ErrorResponse(errorCode, messageUtils));
    }
} 