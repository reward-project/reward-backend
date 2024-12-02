package com.outsider.reward.domain.member.exception;

import com.outsider.reward.global.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(404, "error.member.not.found"),
    DUPLICATE_EMAIL(400, "error.duplicate.email"),
    DUPLICATE_NICKNAME(400, "error.duplicate.nickname"),
    INVALID_PASSWORD(400, "error.invalid.password"),
    EMAIL_NOT_VERIFIED(400, "error.email.not.verified"),
    INVALID_VERIFICATION_CODE(400, "error.invalid.verification.code"),
    MEMBER_ALREADY_EXISTS(400, "error.member.already.exists"),
    INVALID_REFRESH_TOKEN(400, "error.invalid.refresh.token"),
    UNAUTHORIZED_TOKEN(401, "error.unauthorized.token"),
    INVALID_GOOGLE_TOKEN(400, "error.invalid.google.token"),
    GOOGLE_AUTH_FAILED(400, "error.google.auth.failed"),
    DUPLICATE_ROLE(400, "error.duplicate.role");

    private final int status;
    private final String messageKey;

 

    @Override
    public String getCode() {
        return this.name();
    }
} 