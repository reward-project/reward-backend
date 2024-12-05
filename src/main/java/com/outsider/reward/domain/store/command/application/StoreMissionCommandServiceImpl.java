package com.outsider.reward.domain.store.command.application;

import com.outsider.reward.domain.store.command.domain.StoreMission;
import com.outsider.reward.domain.store.command.domain.StoreMissionRepository;
import com.outsider.reward.domain.store.command.domain.service.StoreMissionDomainService;
import com.outsider.reward.domain.store.command.dto.CreateStoreMissionRequest;
import com.outsider.reward.domain.store.command.dto.StoreMissionResponse;
import com.outsider.reward.domain.store.command.mapper.StoreMissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreMissionCommandServiceImpl implements StoreMissionCommandService {

    private final StoreMissionRepository storeMissionRepository;
    private final StoreMissionDomainService storeMissionDomainService;
    private final StoreMissionMapper storeMissionMapper;

    @Override
    @Transactional
    public StoreMissionResponse createStoreMission(CreateStoreMissionRequest request) {
        StoreMission storeMission = storeMissionMapper.toEntity(request);
        storeMissionDomainService.validateStoreMission(storeMission);
        StoreMission savedMission = storeMissionRepository.save(storeMission);
        return storeMissionMapper.toResponse(savedMission);
    }
}
