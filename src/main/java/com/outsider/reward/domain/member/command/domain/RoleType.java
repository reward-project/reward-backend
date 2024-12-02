package com.outsider.reward.domain.member.command.domain;

public enum RoleType {
    ROLE_ADMIN("관리자"),
    ROLE_USER("앱사용자"),
    ROLE_BUSINESS("비즈니스관계자");

    private final String description;

    RoleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 