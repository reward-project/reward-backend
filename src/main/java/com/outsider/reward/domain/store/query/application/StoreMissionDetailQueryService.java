package com.outsider.reward.domain.store.query.application;

import com.outsider.reward.domain.store.query.dto.StoreMissionQueryDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface StoreMissionDetailQueryService {
    Optional<StoreMissionQueryDto> findById(Long id);
    List<StoreMissionQueryDto> findByRegistrantId(Long registrantId);
    List<StoreMissionQueryDto> findByRewardId(String rewardId);
}
