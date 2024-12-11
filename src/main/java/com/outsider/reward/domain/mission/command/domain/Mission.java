package com.outsider.reward.domain.mission.command.domain;

import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.store.command.domain.RewardBudget;
import com.outsider.reward.domain.store.command.domain.RewardUsage;
import com.outsider.reward.domain.tag.command.domain.Tag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

    @OneToOne(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private RewardBudget budget;

    @Column(name = "max_rewards_per_day", nullable = false)
    private int maxRewardsPerDay;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RewardUsage> rewardUsages = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "mission_tags",
        joinColumns = @JoinColumn(name = "mission_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    protected Mission(String rewardName, Member registrant,
                   LocalDate startDate, LocalDate endDate, 
                   Double rewardAmount, int maxRewardsPerDay,
                   Set<Tag> tags) {
        this.rewardId = UUID.randomUUID().toString();
        this.rewardName = rewardName;
        this.registrant = registrant;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rewardAmount = rewardAmount;
        this.maxRewardsPerDay = maxRewardsPerDay;
        this.tags = tags != null ? tags : new HashSet<>();
    }

    public abstract boolean validateAnswer(String answer);
    
    public void initializeBudget(double totalBudget) {
        this.budget = new RewardBudget(this, totalBudget, this.maxRewardsPerDay);
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
        return budget != null ? budget.getRemainingBudget() : 0;
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
}