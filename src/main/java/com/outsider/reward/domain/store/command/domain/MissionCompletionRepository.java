package com.outsider.reward.domain.store.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface MissionCompletionRepository extends JpaRepository<MissionCompletion, Long> {
    boolean existsByUserIdAndMissionId(Long userId, Long missionId);
    
    @Query("SELECT mc.mission.id FROM MissionCompletion mc WHERE mc.userId = :userId")
    List<Long> findCompletedMissionIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT mc.mission.id FROM MissionCompletion mc WHERE mc.userId = :userId AND mc.mission.id IN :missionIds")
    Set<Long> findCompletedMissionIdsByUserIdAndMissionIds(@Param("userId") Long userId, @Param("missionIds") List<Long> missionIds);
}
