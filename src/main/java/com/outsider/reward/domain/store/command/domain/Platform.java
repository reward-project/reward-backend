package com.outsider.reward.domain.store.command.domain;

public enum Platform {
    COUPANG("쿠팡"),
    NAVER("네이버"),
    GMARKET("지마켓");

    private final String displayName;

    Platform(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
