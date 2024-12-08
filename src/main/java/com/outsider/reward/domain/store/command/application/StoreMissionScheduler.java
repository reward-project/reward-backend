package com.outsider.reward.domain.store.command.application;

import com.outsider.reward.domain.store.command.domain.StoreMission;
import com.outsider.reward.domain.store.command.domain.StoreMissionRepository;
import com.outsider.reward.domain.store.command.domain.service.StoreMissionDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StoreMissionScheduler {
    private final StoreMissionRepository storeMissionRepository;
    private final StoreMissionDomainService storeMissionDomainService;

    @Scheduled(cron = "0 0 0 * * *")  // 매일 자정에 실행
    @Transactional
    public void refundExpiredMissions() {
        List<StoreMission> expiredMissions = storeMissionRepository.findExpiredMissionsWithRemainingBudget();
        for (StoreMission mission : expiredMissions) {
            storeMissionDomainService.refundMissionBudget(mission);
        }
    }
} 