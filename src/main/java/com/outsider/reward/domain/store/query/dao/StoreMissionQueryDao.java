package com.outsider.reward.domain.store.query.dao;

import com.outsider.reward.domain.store.command.domain.StoreMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StoreMissionQueryDao extends JpaRepository<StoreMission, Long> {
    
    @Query("SELECT sm FROM StoreMission sm WHERE sm.registrantId = :registrantId")
    List<StoreMission> findAllByRegistrantId(@Param("registrantId") String registrantId);
    
    @Query("SELECT sm FROM StoreMission sm WHERE sm.rewardId = :rewardId")
    List<StoreMission> findAllByRewardId(@Param("rewardId") String rewardId);
    
    @Query("SELECT sm FROM StoreMission sm WHERE sm.startDate <= :date AND sm.endDate >= :date")
    List<StoreMission> findAllActiveMissions(@Param("date") LocalDate date);
    
    @Query("SELECT sm FROM StoreMission sm WHERE sm.registrantId = :registrantId AND sm.startDate <= :date AND sm.endDate >= :date")
    List<StoreMission> findActiveRegistrantMissions(@Param("registrantId") String registrantId, @Param("date") LocalDate date);
}
