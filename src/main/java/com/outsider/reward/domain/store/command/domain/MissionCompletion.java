package com.outsider.reward.domain.store.command.domain;

import com.outsider.reward.domain.mission.command.domain.Mission;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MissionCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    private Double rewardPoint;
    private LocalDateTime completedAt;

    @Builder
    public MissionCompletion(Long userId, Mission mission) {
        this.userId = userId;
        this.mission = mission;
        this.rewardPoint = mission.getRewardAmount();
        this.completedAt = LocalDateTime.now();
    }
}
