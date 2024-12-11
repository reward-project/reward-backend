package com.outsider.reward.domain.mission.exception;

import com.outsider.reward.global.common.exception.BusinessException;
import com.outsider.reward.global.common.exception.ErrorCode;

public class MissionException extends BusinessException {
    
    public MissionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MissionException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
