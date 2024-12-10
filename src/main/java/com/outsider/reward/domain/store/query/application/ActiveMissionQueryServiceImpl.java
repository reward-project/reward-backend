package com.outsider.reward.domain.store.query.application;

import com.outsider.reward.domain.store.command.domain.StoreMission;
import com.outsider.reward.domain.store.command.domain.StoreMissionRepository;
import com.outsider.reward.domain.store.command.domain.MissionCompletionRepository;
import com.outsider.reward.domain.store.exception.StoreMissionErrorCode;
import com.outsider.reward.domain.store.exception.StoreMissionException;
import com.outsider.reward.domain.store.query.dto.StoreMissionQueryResponse;
import com.outsider.reward.domain.store.query.mapper.StoreMissionQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActiveMissionQueryServiceImpl implements ActiveMissionQueryService {

    private final StoreMissionRepository storeMissionRepository;
    private final MissionCompletionRepository missionCompletionRepository;
    private final StoreMissionQueryMapper storeMissionMapper;

    @Override
    public Page<StoreMissionQueryResponse> getStoreMissions(Long userId, LocalDate date, Pageable pageable) {
        return storeMissionRepository.findAllActiveMissionsWithCompletionStatus(
            userId, 
            date, 
            pageable
        );
    }

    @Override
    public StoreMissionQueryResponse getStoreMission(Long userId, Long id) {
        StoreMission mission = storeMissionRepository.findById(id)
            .orElseThrow(() -> new StoreMissionException(StoreMissionErrorCode.STORE_MISSION_NOT_FOUND));
        boolean completed = missionCompletionRepository.existsByUserIdAndMissionId(userId, id);
        return storeMissionMapper.toResponse(mission, completed);
    }

    public Page<StoreMissionQueryResponse> getStoreMissionsForUser(Long userId, LocalDate date, Pageable pageable) {
        List<StoreMission> allMissions = storeMissionRepository.findAllActiveMissions(date);
        Set<Long> completedMissionIds = missionCompletionRepository.findCompletedMissionIdsByUserId(userId)
            .stream()
            .collect(Collectors.toSet());
        
        List<StoreMissionQueryResponse> pagedContent = allMissions.stream()
            .skip(pageable.getOffset())
            .limit(pageable.getPageSize())
            .map(mission -> storeMissionMapper.toResponse(mission, completedMissionIds.contains(mission.getId())))
            .collect(Collectors.toList());
            
        return new PageImpl<>(pagedContent, pageable, allMissions.size());
    }
}
