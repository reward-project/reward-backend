package com.outsider.reward.domain.store.command.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStoreMissionRequest {
    @NotNull
    private Long id;

    @NotNull
    private Long platformId;

    @NotBlank
    private String rewardName;

    @NotBlank
    private String storeName;

    @NotBlank
    private String productLink;

    @NotBlank
    private String keyword;

    @NotBlank
    private String productId;

    @NotNull
    @Positive
    private Long rewardAmount;

    @NotNull
    @Positive
    private Long maxRewardsPerDay;

    private Long totalBudget;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private List<String> tags;
}
