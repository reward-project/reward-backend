package com.outsider.reward.domain.store.query.dto.common;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Getter
@SuperBuilder
public abstract class BaseTimeInfo {
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
} 