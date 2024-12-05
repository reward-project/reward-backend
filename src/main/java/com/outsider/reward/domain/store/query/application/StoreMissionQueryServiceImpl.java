package com.outsider.reward.domain.store.query.application;

import com.outsider.reward.domain.store.command.domain.StoreMission;
import com.outsider.reward.domain.store.command.domain.StoreMissionRepository;
import com.outsider.reward.domain.store.command.mapper.StoreMissionMapper;
import com.outsider.reward.domain.store.query.dto.StoreMissionQueryDto;
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
    private final StoreMissionMapper storeMissionMapper;

    @Override
    public Optional<StoreMissionQueryDto> findById(Long id) {
        return storeMissionRepository.findById(id)
                .map(storeMissionMapper::toQueryDto);
    }

    @Override
    public List<StoreMissionQueryDto> findByRegistrantId(String registrantId) {
        return storeMissionRepository.findByRegistrantId(registrantId).stream()
                .map(storeMissionMapper::toQueryDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<StoreMissionQueryDto> findByRewardId(String rewardId) {
        return storeMissionRepository.findByRewardId(rewardId).stream()
                .map(storeMissionMapper::toQueryDto)
                .collect(Collectors.toList());
    }
}
