package com.outsider.reward.domain.store.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RewardUsageRepository extends JpaRepository<RewardUsage, Long> {
    List<RewardUsage> findByStoreMissionId(Long storeMissionId);
    List<RewardUsage> findByStoreMissionIdAndUsedAtBetween(Long storeMissionId, LocalDateTime start, LocalDateTime end);
    int countByStoreMissionIdAndStatus(Long storeMissionId, RewardUsageStatus status);
    int countByStoreMissionIdAndUsedAtBetweenAndStatus(Long storeMissionId, LocalDateTime start, LocalDateTime end, RewardUsageStatus status);
} 