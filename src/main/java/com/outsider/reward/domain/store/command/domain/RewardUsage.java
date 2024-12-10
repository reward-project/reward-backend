package com.outsider.reward.domain.store.command.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.store.exception.StoreMissionErrorCode;
import com.outsider.reward.domain.store.exception.StoreMissionException;
import com.outsider.reward.domain.tag.command.domain.BaseTimeEntity;

@Entity
@Table(
    name = "reward_usages",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_reward_usage_user_mission",
            columnNames = {"user_id", "store_mission_id"}
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
    @JoinColumn(name = "store_mission_id")
    private StoreMission storeMission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member user;

    @Column(nullable = false)
    private double amount;
    
    @Enumerated(EnumType.STRING)
    private RewardUsageStatus status;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    public RewardUsage(StoreMission storeMission, Member user, double amount) {
        this.storeMission = storeMission;
        this.user = user;
        this.amount = amount;
        this.status = RewardUsageStatus.PENDING;  // 초기 상태는 PENDING
    }

    public void complete() {
        this.status = RewardUsageStatus.COMPLETED;
        this.usedAt = LocalDateTime.now();
    }

    public void fail() {
        this.status = RewardUsageStatus.FAILED;
    }

    public static void   validateUserCanUseReward(Member user, StoreMission mission) {
        if (mission.hasUserUsedReward(user)) {
            throw new StoreMissionException(StoreMissionErrorCode.ALREADY_USED_REWARD);
        }
    }
} 