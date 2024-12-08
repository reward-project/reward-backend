package com.outsider.reward.domain.finance.exception;

import com.outsider.reward.global.common.exception.BusinessException;

public class PaymentException extends BusinessException {
    public PaymentException(PaymentErrorCode errorCode) {
        super(errorCode);
    }
} 