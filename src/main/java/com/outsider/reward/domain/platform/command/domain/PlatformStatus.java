package com.outsider.reward.domain.platform.command.domain;

public enum PlatformStatus {
    PENDING("대기중"),
    ACTIVE("활성"),
    INACTIVE("비활성"),
    REJECTED("거절됨");

    private final String displayName;

    PlatformStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}