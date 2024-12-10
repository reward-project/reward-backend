package com.outsider.reward.domain.store.query.application;

import com.outsider.reward.domain.store.query.dto.StoreMissionQueryResponse;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface ActiveMissionQueryService {
    Page<StoreMissionQueryResponse> getStoreMissions(Long userId, LocalDate date,Pageable pageable);
    StoreMissionQueryResponse getStoreMission(Long userId, Long id);
}