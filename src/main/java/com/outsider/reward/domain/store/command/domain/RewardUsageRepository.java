package com.outsider.reward.domain.store.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RewardUsageRepository extends JpaRepository<RewardUsage, Long> {
    List<RewardUsage> findByMissionId(Long missionId);
    List<RewardUsage> findByMissionIdAndUsedAtBetween(Long missionId, LocalDateTime start, LocalDateTime end);
    int countByMissionIdAndStatus(Long missionId, RewardUsageStatus status);
    int countByMissionIdAndUsedAtBetweenAndStatus(Long missionId, LocalDateTime start, LocalDateTime end, RewardUsageStatus status);
    int countByStatus(RewardUsageStatus status);
}