package com.outsider.reward.domain.store.query.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class RegistrantInfo {
    private Long registrantId;
    private String registrantName;
    private String registrantEmail;
    private String registrantRole;
}