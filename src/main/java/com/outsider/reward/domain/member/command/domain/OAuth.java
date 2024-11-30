package com.outsider.reward.domain.member.command.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
public class OAuth {
    private String provider;
    private String providerId;
    private boolean isOAuthUser;

    public OAuth(String provider, String providerId) {
        this.provider = provider;
        this.providerId = providerId;
        this.isOAuthUser = true;
    }
} 