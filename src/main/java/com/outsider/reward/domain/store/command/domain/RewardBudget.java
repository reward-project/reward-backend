package com.outsider.reward.domain.store.command.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.outsider.reward.domain.tag.command.domain.BaseTimeEntity;

@Entity
@Table(name = "reward_budgets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RewardBudget extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_mission_id")
    private StoreMission storeMission;

    @Column(nullable = false)
    private double totalBudget;        // 총 예산

    @Column(nullable = false)
    private double remainingBudget;    // 남은 예산

    @Column(nullable = false)
    private double dailyBudget;        // 일일 예산

    @Column(nullable = false)
    private double todayUsedBudget;    // 오늘 사용된 예산

    @Column(nullable = false)
    private int maxRewardsPerDay;      // 일일 최대 리워드 수

    @Column(nullable = false)
    private int todayRewardCount;      // 오늘 사용된 리워드 수

    @Column(nullable = false)
    private LocalDateTime lastResetTime;  // 마지막 일일 리셋 시간

    public RewardBudget(StoreMission storeMission, double totalBudget, int maxRewardsPerDay) {
        this.storeMission = storeMission;
        this.totalBudget = totalBudget;
        this.remainingBudget = totalBudget;
        this.dailyBudget = totalBudget / storeMission.getDurationInDays();
        this.maxRewardsPerDay = maxRewardsPerDay;
        this.todayUsedBudget = 0;
        this.todayRewardCount = 0;
        this.lastResetTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public boolean canUseReward(double amount) {
        checkAndResetDaily();
        return remainingBudget >= amount &&
               todayUsedBudget + amount <= dailyBudget &&
               todayRewardCount < maxRewardsPerDay;
    }

    public void useReward(double amount) {
        if (!canUseReward(amount)) {
            throw new IllegalStateException("Cannot use reward: Budget or daily limit exceeded");
        }
        remainingBudget -= amount;
        todayUsedBudget += amount;
        todayRewardCount++;
    }

    private void checkAndResetDaily() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
        if (lastResetTime.isBefore(todayStart)) {
            todayUsedBudget = 0;
            todayRewardCount = 0;
            lastResetTime = todayStart;
        }
    }

    public double getUsageRate() {
        return ((totalBudget - remainingBudget) / totalBudget) * 100;
    }

    public boolean isExhausted() {
        return remainingBudget <= 0;
    }
} 