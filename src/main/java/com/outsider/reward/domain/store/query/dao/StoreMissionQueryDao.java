package com.outsider.reward.domain.store.query.dao;

import com.outsider.reward.domain.store.command.domain.StoreMission;
import com.outsider.reward.domain.store.query.StoreMissionQuery;
import com.outsider.reward.domain.store.query.dto.StoreMissionQueryDto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StoreMissionQueryDao extends JpaRepository<StoreMission, Long>, StoreMissionQueryDaoCustom {
    
    @Query("SELECT sm FROM StoreMission sm WHERE sm.registrant.id = :registrantId")
    List<StoreMission> findAllByRegistrantId(@Param("registrantId") Long registrantId);
    
    @Query("SELECT sm FROM StoreMission sm WHERE sm.rewardId = :rewardId")
    List<StoreMission> findAllByRewardId(@Param("rewardId") String rewardId);
    
    @Query("SELECT sm FROM StoreMission sm WHERE sm.startDate <= :date AND sm.endDate >= :date")
    List<StoreMission> findAllActiveMissions(@Param("date") LocalDate date);
    
    @Query("SELECT sm FROM StoreMission sm WHERE sm.registrant.id = :registrantId AND sm.startDate <= :date AND sm.endDate >= :date")
    List<StoreMission> findActiveRegistrantMissions(@Param("registrantId") Long registrantId, @Param("date") LocalDate date);
}
