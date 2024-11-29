package com.outsider.reward.domain.member.command.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class OAuth {
    private String provider;
    private String providerId;
} 