package com.outsider.reward.domain.store.command.dto;

import com.outsider.reward.domain.store.query.dto.PlatformInfo;
import com.outsider.reward.domain.store.query.dto.RewardInfo;
import com.outsider.reward.domain.store.query.dto.StoreInfo;
import com.outsider.reward.domain.store.query.dto.RegistrantInfo;
import com.outsider.reward.domain.store.query.dto.common.BaseTimeInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@SuperBuilder
public class StoreMissionResponse extends BaseTimeInfo {
    private Long id;
    private PlatformInfo platform;
    private RewardInfo reward;
    private StoreInfo store;
    private RegistrantInfo registrant;
    private int totalUsageCount;
    private int todayUsageCount;
    private double usageRate;
    private Map<Integer, Integer> usageByHour;
    private Map<String, Integer> usageByDay;
    private List<RewardUsageResponse> recentUsages;

    @Getter
    @SuperBuilder
    public static class RewardUsageResponse {
        private Long id;
        private String memberName;
        private LocalDateTime usedAt;
    }
}
