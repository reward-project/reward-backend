package com.outsider.reward.domain.store.command.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.outsider.reward.domain.finance.command.domain.RewardBudget;
import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.mission.command.domain.Mission;
import com.outsider.reward.domain.platform.command.domain.Platform;
import com.outsider.reward.domain.store.exception.StoreMissionErrorCode;
import com.outsider.reward.domain.store.exception.StoreMissionException;
import com.outsider.reward.domain.tag.command.domain.Tag;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "store_missions")
@DiscriminatorValue("STORE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class StoreMission extends Mission {

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


    @Builder
    public StoreMission(String rewardName, Platform platform, String storeName, Member registrant,
                       String productLink, String keyword, String productId,
                       LocalDate startDate, LocalDate endDate,
                       Double rewardAmount, int maxRewardsPerDay, Set<Tag> tags) {
        super(rewardName, registrant, startDate, endDate, rewardAmount, maxRewardsPerDay, tags);
        this.platform = platform;
        this.storeName = storeName;
        this.productLink = productLink;
        this.keyword = keyword;
        this.productId = productId;
    }

    @Override
    public boolean validateAnswer(String answer) {
        return productId.equals(answer);
    }

    @Override
    public boolean canParticipate() {
        return isActive() && getRemainingBudget() > 0;
    }

    public Set<String> getTagNames() {
        return getTags().stream()
            .map(Tag::getName)
            .collect(Collectors.toSet());
    }

    public void updateTags(Set<Tag> newTags) {
        getTags().clear();
        getTags().addAll(newTags);
    }

    public void addTag(Tag tag) {
        getTags().add(tag);
    }

    public void removeTag(Tag tag) {
        getTags().remove(tag);
    }

    public int getTotalUsageCount() {
        return getRewardUsages().size();
    }

    public long getTotalRewardUsage() {
        return getRewardUsages().stream()
            .filter(usage -> usage.getStatus() == RewardUsageStatus.COMPLETED)
            .mapToLong(usage -> (long) usage.getAmount())
            .sum();
    }

    public int getTodayUsageCount() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return (int) getRewardUsages().stream()
            .filter(usage -> usage.getUsedAt().isAfter(startOfDay) && 
                           usage.getUsedAt().isBefore(endOfDay))
            .count();
    }

    public double getUsageRate() {
        Set<RewardUsage> usages = getRewardUsages();
        if (usages.isEmpty()) return 0.0;
        long completedCount = usages.stream()
            .filter(usage -> usage.getStatus() == RewardUsageStatus.COMPLETED)
            .count();
        return (double) completedCount / usages.size() * 100;
    }

    public Map<Integer, Integer> getUsageByHour() {
        return getRewardUsages().stream()
            .collect(Collectors.groupingBy(
                usage -> usage.getUsedAt().getHour(),
                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            ));
    }

    public Map<String, Integer> getUsageByDay() {
        return getRewardUsages().stream()
            .collect(Collectors.groupingBy(
                usage -> usage.getUsedAt().getDayOfWeek().toString(),
                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            ));
    }

    public int getDurationInDays() {
        return (int) ChronoUnit.DAYS.between(getStartDate(), getEndDate()) + 1;
    }

    public double getBudgetUsageRate() {
        RewardBudget budget = getBudget();
        return budget != null ? budget.getUsageRate() : 0;
    }

    public double refundRemainingBudget() {
        if (!isExpired()) {
            throw new StoreMissionException(StoreMissionErrorCode.MISSION_NOT_EXPIRED);
        }

        RewardBudget budget = getBudget();
        double remainingAmount = budget.getRemainingBudget();
        if (remainingAmount <= 0) {
            return 0;
        }

        budget.setRemainingAmount(0);
        return remainingAmount;
    }


    public void updateReward(
            String rewardName,
            double rewardAmount,
            int maxRewardsPerDay,
            LocalDate startDate,
            LocalDate endDate) {
        super.updateReward(rewardName, rewardAmount, maxRewardsPerDay, startDate, endDate);
    }

    public void updateStore(
            String storeName,
            String productLink,
            String keyword,
            String productId) {
        this.storeName = storeName;
        this.productLink = productLink;
        this.keyword = keyword;
        this.productId = productId;
    }

    public void updatePlatform(Platform platform) {
        this.platform = platform;
    }

    public Long getRegistrantId() {
        return getRegistrant().getId();
    }
}
