package com.outsider.reward.domain.tag.exception;

import com.outsider.reward.global.common.exception.BusinessException;
import com.outsider.reward.global.common.exception.ErrorCode;

public class TagException extends BusinessException {
    public TagException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TagException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
} 