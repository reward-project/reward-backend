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
    private final String status;  // 미션 상태 추가
}
