package com.outsider.reward.domain.finance.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RewardBudgetRepository extends JpaRepository<RewardBudget, Long> {
    @Query("SELECT rb FROM RewardBudget rb " +
           "WHERE rb.mission.registrant.id = :memberId " +
           "AND rb.mission.startDate <= :currentDate " +
           "AND rb.mission.endDate >= :currentDate " +
           "AND rb.usedBudget < rb.totalBudget")
    List<RewardBudget> findAllByMemberIdAndStatusIn(@Param("memberId") Long memberId, @Param("currentDate") LocalDate currentDate);

    Optional<RewardBudget> findByMissionId(Long missionId);
}
