package com.outsider.reward.domain.store.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreMissionRepository extends JpaRepository<StoreMission, Long> {
    @Query("SELECT sm FROM StoreMission sm " +
           "LEFT JOIN FETCH sm.rewardUsages " +
           "WHERE sm.registrant.id = :registrantId")
    List<StoreMission> findByRegistrantIdWithRewardUsages(@Param("registrantId") Long registrantId);

    List<StoreMission> findByRewardId(String rewardId);
    Optional<StoreMission> findByIdAndRegistrant_Id(Long id, Long registrantId);

    @Query("SELECT sm FROM StoreMission sm JOIN sm.tags t WHERE t.name = :tagName")
    List<StoreMission> findByTagName(@Param("tagName") String tagName);

    @Query("SELECT m FROM StoreMission m WHERE m.endDate < CURRENT_DATE AND m.budget.remainingBudget > 0")
    List<StoreMission> findExpiredMissionsWithRemainingBudget();
}
