package com.outsider.reward.domain.member.exception;

import com.outsider.reward.common.exception.BusinessException;
import com.outsider.reward.common.exception.ErrorCode;

public class MemberException extends BusinessException {
    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
} 