package com.outsider.reward.domain.store.command.domain;

import com.outsider.reward.domain.mission.command.domain.Mission;
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

    @OneToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @Column(nullable = false)
    private double totalBudget;

    @Column(nullable = false)
    private double usedBudget;

    @Column(nullable = false)
    private int maxRewardsPerDay;

    @Column(nullable = false)
    private int usedRewardsToday;

    @Column(nullable = false)
    private LocalDateTime lastRewardDate;

    public RewardBudget(Mission mission, double totalBudget, int maxRewardsPerDay) {
        this.mission = mission;
        this.totalBudget = totalBudget;
        this.maxRewardsPerDay = maxRewardsPerDay;
        this.usedBudget = 0;
        this.usedRewardsToday = 0;
        this.lastRewardDate = LocalDateTime.now();
    }

    public boolean canUseReward(double amount) {
        return getRemainingBudget() >= amount && !isDailyLimitExceeded();
    }

    public void useReward(double amount) {
        if (!canUseReward(amount)) {
            throw new IllegalStateException("Cannot use reward: either insufficient budget or daily limit exceeded");
        }
        this.usedBudget += amount;
        incrementDailyUsage();
    }

    public double getRemainingBudget() {
        return totalBudget - usedBudget;
    }

    public void setRemainingAmount(double amount) {
        this.usedBudget = this.totalBudget - amount;
    }

    public boolean isExhausted() {
        return getRemainingBudget() <= 0;
    }

    private boolean isDailyLimitExceeded() {
        updateDailyUsage();
        return usedRewardsToday >= maxRewardsPerDay;
    }

    private void incrementDailyUsage() {
        updateDailyUsage();
        usedRewardsToday++;
        lastRewardDate = LocalDateTime.now();
    }

    private void updateDailyUsage() {
        LocalDateTime now = LocalDateTime.now();
        if (!now.toLocalDate().equals(lastRewardDate.toLocalDate())) {
            usedRewardsToday = 0;
            lastRewardDate = now;
        }
    }

    public double getUsageRate() {
        if (totalBudget == 0) return 0;
        return (usedBudget / totalBudget) * 100;
    }
}