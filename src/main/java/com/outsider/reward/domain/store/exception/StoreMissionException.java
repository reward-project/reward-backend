package com.outsider.reward.domain.store.exception;

import com.outsider.reward.global.common.exception.BusinessException;
import com.outsider.reward.global.common.exception.ErrorCode;

public class StoreMissionException extends BusinessException {
    
    public StoreMissionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public StoreMissionException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
