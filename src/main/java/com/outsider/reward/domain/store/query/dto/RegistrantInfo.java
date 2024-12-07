package com.outsider.reward.domain.store.query.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class RegistrantInfo {
    private Long registrantId;
    private String registrantName;
    private String registrantEmail;
    private String registrantRole;
} 