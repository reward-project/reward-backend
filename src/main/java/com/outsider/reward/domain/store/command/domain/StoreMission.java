package com.outsider.reward.domain.store.command.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.outsider.reward.domain.finance.command.domain.Account;
import com.outsider.reward.domain.finance.command.domain.Transaction;
import com.outsider.reward.domain.finance.command.domain.TransactionType;
import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.platform.command.domain.Platform;
import com.outsider.reward.domain.store.exception.StoreMissionErrorCode;
import com.outsider.reward.domain.store.exception.StoreMissionException;
import com.outsider.reward.domain.tag.command.domain.Tag;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "store_missions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class StoreMission {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "product_link", nullable = false)
    private String productLink;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "option_id", nullable = false)
    private String optionId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @OneToOne(mappedBy = "storeMission", cascade = CascadeType.ALL, orphanRemoval = true)
    private RewardBudget budget;

    @Column(name = "max_rewards_per_day", nullable = false)
    private int maxRewardsPerDay;

    @OneToMany(mappedBy = "storeMission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RewardUsage> rewardUsages = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "store_mission_tags",
        joinColumns = @JoinColumn(name = "store_mission_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Builder
    public StoreMission(String rewardName, Platform platform, String storeName, Member registrant,
                       String productLink, String keyword, String productId, String optionId,
                       LocalDate startDate, LocalDate endDate, Double rewardAmount, int maxRewardsPerDay,
                       Set<Tag> tags) {
        this.rewardId = UUID.randomUUID().toString();
        this.rewardName = rewardName;
        this.platform = platform;
        this.storeName = storeName;
        this.registrant = registrant;
        this.productLink = productLink;
        this.keyword = keyword;
        this.productId = productId;
        this.optionId = optionId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rewardAmount = rewardAmount;
        this.maxRewardsPerDay = maxRewardsPerDay;
        this.tags = tags != null ? tags : new HashSet<>();
    }

    public void validateDates() {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }

    public Set<String> getTagNames() {
        return tags.stream()
            .map(Tag::getName)
            .collect(Collectors.toSet());
    }

    public void updateTags(List<Tag> newTags) {
        this.tags.clear();
        this.tags.addAll(newTags);
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }

    public int getTotalUsageCount() {
        return rewardUsages.size();
    }

    public int getTodayUsageCount() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return (int) rewardUsages.stream()
            .filter(usage -> usage.getUsedAt().isAfter(startOfDay) && 
                           usage.getUsedAt().isBefore(endOfDay))
            .count();
    }

    public double getUsageRate() {
        if (rewardUsages.isEmpty()) return 0.0;
        long completedCount = rewardUsages.stream()
            .filter(usage -> usage.getStatus() == RewardUsageStatus.COMPLETED)
            .count();
        return (double) completedCount / rewardUsages.size() * 100;
    }

    public Map<Integer, Integer> getUsageByHour() {
        return rewardUsages.stream()
            .collect(Collectors.groupingBy(
                usage -> usage.getUsedAt().getHour(),
                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            ));
    }

    public Map<String, Integer> getUsageByDay() {
        return rewardUsages.stream()
            .collect(Collectors.groupingBy(
                usage -> usage.getUsedAt().getDayOfWeek().toString(),
                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            ));
    }

    public int getDurationInDays() {
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    public void initializeBudget(double totalBudget) {
        this.budget = new RewardBudget(this, totalBudget, this.maxRewardsPerDay);
    }

    public boolean canUseReward(double amount) {
        return budget != null && budget.canUseReward(amount);
    }

    public void useReward(double amount) {
        if (budget == null) {
            throw new IllegalStateException("Budget not initialized");
        }
        budget.useReward(amount);
    }

    public double getRemainingBudget() {
        return budget != null ? budget.getRemainingBudget() : 0;
    }

    public double getBudgetUsageRate() {
        return budget != null ? budget.getUsageRate() : 0;
    }

    public boolean hasUserUsedReward(Member user) {
        return rewardUsages.stream()
            .anyMatch(usage -> usage.getUser().equals(user) && 
                     usage.getStatus() == RewardUsageStatus.COMPLETED);
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(endDate);
    }

    public double refundRemainingBudget() {
        if (!isExpired()) {
            throw new StoreMissionException(StoreMissionErrorCode.MISSION_NOT_EXPIRED);
        }

        double remainingAmount = budget.getRemainingBudget();
        if (remainingAmount <= 0) {
            return 0;
        }

        budget.setRemainingAmount(0);
        return remainingAmount;
    }

    public Member getRegistrant() {
        return registrant;
    }

    public long getTotalRewardUsage() {
        return rewardUsages.stream()
            .filter(usage -> usage.getStatus() == RewardUsageStatus.COMPLETED)
            .mapToLong(usage -> (long) usage.getAmount())
            .sum();
    }
}
