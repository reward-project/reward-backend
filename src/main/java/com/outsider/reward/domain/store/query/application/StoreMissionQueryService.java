package com.outsider.reward.domain.store.query.application;

import com.outsider.reward.domain.store.query.dto.StoreMissionQueryDto;

import java.util.List;
import java.util.Optional;

public interface StoreMissionQueryService {
    Optional<StoreMissionQueryDto> findById(Long id);
    List<StoreMissionQueryDto> findByRegistrantId(String registrantId);
    List<StoreMissionQueryDto> findByRewardId(String rewardId);
}
