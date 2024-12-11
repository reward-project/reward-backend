package com.outsider.reward.domain.store.command.domain;

import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.mission.command.domain.Mission;
import com.outsider.reward.domain.mission.exception.MissionErrorCode;
import com.outsider.reward.domain.mission.exception.MissionException;
import com.outsider.reward.domain.tag.command.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "reward_usages",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_reward_usage_user_mission",
            columnNames = {"user_id", "mission_id"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RewardUsage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member user;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    private RewardUsageStatus status;

    private LocalDateTime usedAt;

    public RewardUsage(Mission mission, Member user, double amount) {
        this.mission = mission;
        this.user = user;
        this.amount = amount;
        this.status = RewardUsageStatus.PENDING;
    }

    public void complete() {
        this.status = RewardUsageStatus.COMPLETED;
        this.usedAt = LocalDateTime.now();
    }

    public void fail() {
        this.status = RewardUsageStatus.FAILED;
    }

    public static void validateUserCanUseReward(Member user, Mission mission) {
        if (mission.hasUserUsedReward(user)) {
            throw new MissionException(MissionErrorCode.ALREADY_USED_REWARD);
        }
    }
}