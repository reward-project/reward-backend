package com.outsider.reward.domain.store.command.application;

import com.outsider.reward.domain.store.command.dto.CreateStoreMissionRequest;
import com.outsider.reward.domain.store.command.dto.StoreMissionResponse;

public interface StoreMissionCommandService {
    StoreMissionResponse createStoreMission(CreateStoreMissionRequest request);
}
