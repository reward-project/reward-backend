package com.outsider.reward.domain.store.command.dto;

import com.outsider.reward.domain.store.query.dto.PlatformInfo;
import com.outsider.reward.domain.store.query.dto.RewardInfo;
import com.outsider.reward.domain.store.query.dto.StoreInfo;
import com.outsider.reward.domain.store.query.dto.RegistrantInfo;
import com.outsider.reward.domain.store.query.dto.common.BaseTimeInfo;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@SuperBuilder
public class StoreMissionResponse extends BaseTimeInfo {
    private final Long id;
    private final PlatformInfo platform;
    private final RewardInfo reward;
    private final StoreInfo store;
    private final RegistrantInfo registrant;
    private final int totalUsageCount;
    private final int todayUsageCount;
    private final double usageRate;
    private final Map<Integer, Integer> usageByHour;
    private final Map<String, Integer> usageByDay;
    private final List<RewardUsageResponse> recentUsages;

    @Getter
    @SuperBuilder
    public static class RewardUsageResponse {
        private final LocalDateTime timestamp;
        private final String userId;
        private final String userName;
        private final double amount;
        private final String status;
    }
}
