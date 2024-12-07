package com.outsider.reward.domain.store.query.application;

import com.outsider.reward.domain.store.query.dto.StoreMissionQueryDto;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;


@Service
public interface StoreMissionQueryService {
    Optional<StoreMissionQueryDto> findById(Long id);
    List<StoreMissionQueryDto> findByRegistrantId(Long registrantId);
    List<StoreMissionQueryDto> findByRewardId(String rewardId);
    List<StoreMissionQueryDto> findByTag(String tag);
}
