package com.outsider.reward.domain.platform.exception;

import com.outsider.reward.global.common.exception.BusinessException;
import com.outsider.reward.global.common.exception.ErrorCode;

public class PlatformException extends BusinessException {
    public PlatformException(ErrorCode errorCode) {
        super(errorCode);
    }

    public PlatformException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
