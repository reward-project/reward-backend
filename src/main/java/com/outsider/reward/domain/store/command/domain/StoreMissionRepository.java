package com.outsider.reward.domain.store.command.domain;

import org.apache.catalina.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoreMissionRepository extends JpaRepository<StoreMission, Long> ,StoreMissionRepositoryCustom {
    @Query("SELECT sm FROM StoreMission sm " +
           "LEFT JOIN FETCH sm.rewardUsages " +
           "WHERE sm.registrant.id = :registrantId")
    List<StoreMission> findByRegistrantIdWithRewardUsages(@Param("registrantId") Long registrantId);

    List<StoreMission> findByRewardId(String rewardId);
    Optional<StoreMission> findByIdAndRegistrant_Id(Long id, Long registrantId);

    @Query("SELECT sm FROM StoreMission sm JOIN sm.tags t WHERE t.name = :tagName")
    List<StoreMission> findByTagName(@Param("tagName") String tagName);

    @Query("SELECT m FROM StoreMission m LEFT JOIN m.budget b WHERE m.endDate < CURRENT_DATE AND (b IS NULL OR b.totalBudget - b.usedBudget > 0)")
    List<StoreMission> findExpiredMissionsWithRemainingBudget();

    @Query("SELECT sm FROM StoreMission sm WHERE sm.startDate <= :date AND sm.endDate >= :date")
    List<StoreMission> findAllActiveMissions(@Param("date") LocalDate date);

    @Query("SELECT DISTINCT sm FROM StoreMission sm " +
           "LEFT JOIN FETCH sm.tags " +
           "WHERE (sm.startDate IS NULL OR sm.startDate <= :date) " +
           "AND (sm.endDate IS NULL OR sm.endDate >= :date)")
    Page<StoreMission> findAllActiveMissionsWithTags(@Param("date") LocalDate date, Pageable pageable);

    @Query("SELECT COUNT(sm) FROM StoreMission sm " +
           "WHERE (sm.startDate IS NULL OR sm.startDate <= :date) " +
           "AND (sm.endDate IS NULL OR sm.endDate >= :date)")
    long countActiveMissions(@Param("date") LocalDate date);

    @Query("SELECT sm FROM StoreMission sm " +
           "LEFT JOIN FETCH sm.registrant " +
           "LEFT JOIN FETCH sm.platform " +
           "LEFT JOIN FETCH sm.tags " +
           "WHERE sm.id = :id")
    Optional<StoreMission> findByIdWithRegistrant(@Param("id") Long id);
}
