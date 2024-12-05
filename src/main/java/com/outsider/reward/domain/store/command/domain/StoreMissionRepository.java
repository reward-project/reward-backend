package com.outsider.reward.domain.store.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreMissionRepository extends JpaRepository<StoreMission, Long> {
    List<StoreMission> findByRegistrantId(String registrantId);
    List<StoreMission> findByRewardId(String rewardId);
}
