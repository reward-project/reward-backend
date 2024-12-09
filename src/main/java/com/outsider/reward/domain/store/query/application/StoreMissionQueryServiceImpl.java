package com.outsider.reward.domain.store.query.application;

import com.outsider.reward.domain.store.command.domain.StoreMission;
import com.outsider.reward.domain.store.command.domain.StoreMissionRepository;
import com.outsider.reward.domain.store.query.dto.StoreMissionQueryDto;
import com.outsider.reward.domain.store.query.mapper.StoreMissionQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreMissionQueryServiceImpl implements StoreMissionQueryService {

    private final StoreMissionRepository storeMissionRepository;
    private final StoreMissionQueryMapper storeMissionMapper;

    @Override
    public Optional<StoreMissionQueryDto> findById(Long id) {
        return storeMissionRepository.findById(id)
                .map(storeMissionMapper::toDto);
    }

    @Override
    public List<StoreMissionQueryDto> findByRegistrantId(Long registrantId) {
        return storeMissionRepository.findByRegistrantIdWithRewardUsages(registrantId).stream()
                .map(storeMissionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StoreMissionQueryDto> findByRewardId(String rewardId) {
        return storeMissionRepository.findByRewardId(rewardId).stream()
                .map(storeMissionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StoreMissionQueryDto> findByTag(String tag) {
        return storeMissionRepository.findByTagName(tag).stream()
                .map(storeMissionMapper::toDto)
                .collect(Collectors.toList());
    }
}
