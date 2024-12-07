package com.outsider.reward.domain.store.query.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import com.outsider.reward.domain.store.query.dto.common.BaseTimeInfo;

@Getter
@SuperBuilder
public class PlatformInfo extends BaseTimeInfo {
    private final Long id;
    private final String name;
    private final String displayName;
    private final String status;
} 