package com.outsider.reward.domain.store.command.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.outsider.reward.domain.platform.command.domain.Platform;
import com.outsider.reward.domain.tag.command.domain.Tag;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

    @Column(nullable = false)
    private String rewardName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    @Column(nullable = false)
    private String storeName;

    @Column(nullable = false)
    private String registrantName;

    @Column(nullable = false)
    private String productLink;

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private String optionId;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Long registrantId;

    @Column(nullable = false)
    private String rewardId;

    @Column(nullable = false)
    private Double rewardAmount;

    @Column(nullable = false)
    private Integer maxRewardsPerDay;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "store_mission_tags",
        joinColumns = @JoinColumn(name = "store_mission_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Builder
    public StoreMission(String rewardName, Platform platform, String storeName,
                       String registrantName, String productLink, String keyword,
                       String productId, String optionId, LocalDate startDate,
                       LocalDate endDate, String registrantId, Double rewardAmount,
                       Integer maxRewardsPerDay, Set<Tag> tags) {
        this.rewardName = rewardName;
        this.platform = platform;
        this.storeName = storeName;
        this.registrantName = registrantName;
        this.productLink = productLink;
        this.keyword = keyword;
        this.productId = productId;
        this.optionId = optionId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.registrantId = Long.parseLong(registrantId);
        this.rewardAmount = rewardAmount;
        this.maxRewardsPerDay = maxRewardsPerDay;
        this.rewardId = UUID.randomUUID().toString();
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
}
