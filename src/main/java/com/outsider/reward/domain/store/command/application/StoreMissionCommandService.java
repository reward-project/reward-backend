package com.outsider.reward.domain.store.command.application;

import com.outsider.reward.domain.store.command.domain.StoreMission;
import com.outsider.reward.domain.store.command.dto.CreateStoreMissionRequest;
import com.outsider.reward.domain.store.command.dto.StoreMissionResponse;
import com.outsider.reward.domain.store.command.dto.UpdateStoreMissionRequest;

import jakarta.transaction.Transactional;

/**
 * 
 */
public interface StoreMissionCommandService {
    StoreMissionResponse createStoreMission(CreateStoreMissionRequest request);

    @Transactional
    StoreMissionResponse updateStoreMission(Long id, UpdateStoreMissionRequest request, Long userId);

    @Transactional
    void deleteStoreMission(Long id, Long userId);

    StoreMission getStoreMissionById(Long id);

    StoreMission getStoreMissionForEdit(Long id, Long userId);
}
