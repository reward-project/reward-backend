package com.outsider.reward.domain.store.query.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import com.outsider.reward.domain.store.query.dto.common.BaseTimeInfo;

import java.util.Set;

@Getter
@SuperBuilder
public class StoreMissionQueryDto extends BaseTimeInfo {
    private final Long id;
    private final PlatformInfo platform;
    private final RewardInfo reward;
    private final StoreInfo store;
    private final RegistrantInfo registrant;
    private final Set<String> tags;
    private final String status;
    private final long totalRewardUsage;  // 총 리워드 사용량
    private final long remainingRewardBudget;  // 남은 리워드 예산
}
