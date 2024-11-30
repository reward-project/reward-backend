package com.outsider.reward.common.exception;

public interface ErrorCode {
    int getStatus();
    String getMessageKey();
    String getCode();
} 