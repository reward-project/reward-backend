package com.outsider.reward.domain.mission.command.domain;

import com.outsider.reward.domain.finance.command.domain.RewardBudget;
import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.store.command.domain.MissionStatus;
import com.outsider.reward.domain.store.command.domain.RewardUsage;
import com.outsider.reward.domain.tag.command.domain.Tag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "missions")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "mission_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public abstract class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrant_id", nullable = false)
    private Member registrant;

    @Column(name = "reward_id", unique = true, nullable = false)
    private String rewardId;

    @Column(name = "reward_name", nullable = false)
    private String rewardName;

    @Column(name = "reward_amount", nullable = false)
    private Double rewardAmount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "totalBudget", column = @Column(name = "reward_total_budget")),
        @AttributeOverride(name = "usedBudget", column = @Column(name = "reward_used_budget")),
        @AttributeOverride(name = "remainingBudget", column = @Column(name = "reward_remaining_budget"))
    })
    private RewardBudget budget;

    @Column(name = "max_rewards_per_day", nullable = false)
    private int maxRewardsPerDay;

    @Column(name = "total_budget", nullable = false)
    private Long totalBudget;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RewardUsage> rewardUsages = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "mission_tags",
        joinColumns = @JoinColumn(name = "mission_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    protected Mission(String rewardName, Member registrant, LocalDate startDate, LocalDate endDate,
                   Double rewardAmount, int maxRewardsPerDay, Set<Tag> tags) {
        this.rewardId = UUID.randomUUID().toString();
        this.rewardName = rewardName;
        this.registrant = registrant;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rewardAmount = rewardAmount;
        this.maxRewardsPerDay = maxRewardsPerDay;
        this.tags = tags != null ? tags : new HashSet<>();
        initializeTotalBudget();
    }

    private void initializeTotalBudget() {
        int durationInDays = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        this.totalBudget = (long) (rewardAmount * maxRewardsPerDay * durationInDays);
    }

    public Long getTotalBudget() {
        return totalBudget;
    }

    public abstract boolean validateAnswer(String answer);
    
    public void initializeBudget(double totalBudget) {
        this.budget = RewardBudget.create(BigDecimal.valueOf(totalBudget));
    }

    public boolean canUseReward(double amount) {
        return budget != null && budget.canUseReward(amount);
    }

    public void useReward(double amount) {
        if (budget != null) {
            budget.useReward(amount);
        }
    }

    public double getRemainingBudget() {
        return budget != null ? budget.getRemainingBudget().doubleValue() : 0;
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(endDate);
    }

    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    public abstract boolean canParticipate();

    public boolean hasUserUsedReward(Member user) {
        return rewardUsages.stream()
            .anyMatch(usage -> usage.getUser().equals(user));
    }

    public void updateReward(
            String rewardName,
            double rewardAmount,
            int maxRewardsPerDay,
            LocalDate startDate,
            LocalDate endDate) {
        this.rewardName = rewardName;
        this.rewardAmount = rewardAmount;
        this.maxRewardsPerDay = maxRewardsPerDay;
        this.startDate = startDate;
        this.endDate = endDate;
        initializeTotalBudget();
        if (this.budget == null) {
            initializeBudget(rewardAmount * maxRewardsPerDay * (endDate.toEpochDay() - startDate.toEpochDay() + 1));
        } else {
            this.budget.setTotalAmount(rewardAmount * maxRewardsPerDay * (endDate.toEpochDay() - startDate.toEpochDay() + 1));
            this.budget.setMaxRewardsPerDay(maxRewardsPerDay);
        }
    }

    public Member getRegistrant() {
        return registrant;
    }
    public int getTodayUsageCount() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return (int) getRewardUsages().stream()
            .filter(usage -> usage.getUsedAt().isAfter(startOfDay) && 
                           usage.getUsedAt().isBefore(endOfDay))
            .count();
    }

    public MissionStatus getStatus() {
        LocalDate now = LocalDate.now();
        
        // 시작일 이전
        if (now.isBefore(getStartDate())) {
            return MissionStatus.SCHEDULED;
        }
        
        // 종료일 이후
        if (now.isAfter(getEndDate())) {
            return MissionStatus.EXPIRED;
        }
        
        // 오늘 최대 리워드 도달 여부 확인
        if (getTodayUsageCount() >= getMaxRewardsPerDay()) {
            return MissionStatus.DAILY_LIMIT_REACHED;
        }
        
        // 총 예산 소진 여부 확인
        RewardBudget budget = getBudget();
        if (budget != null && budget.isExhausted()) {
            return MissionStatus.BUDGET_EXHAUSTED;
        }
        
        return MissionStatus.ACTIVE;
    }
}
