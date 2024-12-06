package com.outsider.reward.domain.store.command.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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
    private String registrantId;

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

    @Builder
    public StoreMission(String rewardName, Platform platform, String storeName,
                       String registrantName, String productLink, String keyword,
                       String productId, String optionId, LocalDate startDate,
                       LocalDate endDate, String registrantId, Double rewardAmount,
                       Integer maxRewardsPerDay) {
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
        this.registrantId = registrantId;
        this.rewardAmount = rewardAmount;
        this.maxRewardsPerDay = maxRewardsPerDay;
        this.rewardId = UUID.randomUUID().toString();
    }

    public void validateDates() {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }
}
