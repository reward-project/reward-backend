package com.outsider.reward.domain.store.command.dto;

import com.outsider.reward.domain.store.query.dto.PlatformInfo;
import com.outsider.reward.domain.store.query.dto.RewardInfo;
import com.outsider.reward.domain.store.query.dto.StoreInfo;
import com.outsider.reward.domain.store.query.dto.RegistrantInfo;
import com.outsider.reward.domain.store.query.dto.common.BaseTimeInfo;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class StoreMissionResponse extends BaseTimeInfo {
    private final Long id;
    private final PlatformInfo platform;
    private final RewardInfo reward;
    private final StoreInfo store;
    private final RegistrantInfo registrant;
}
