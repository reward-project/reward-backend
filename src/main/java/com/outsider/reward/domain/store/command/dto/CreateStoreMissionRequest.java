package com.outsider.reward.domain.store.command.dto;

import com.outsider.reward.domain.store.command.validator.ValidDateRange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ValidDateRange
public class CreateStoreMissionRequest {
    
    @NotBlank(message = "Reward name is required")
    private String rewardName;

    @NotBlank(message = "Store name is required")
    private String storeName;

    @NotBlank(message = "Product link is required")
    private String productLink;

    @NotBlank(message = "Keyword is required")
    private String keyword;

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotBlank(message = "Option ID is required")
    private String optionId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Registrant ID is required")
    private Long registrantId;

    @NotNull(message = "Reward amount is required")
    private Double rewardAmount;

    @NotNull(message = "Total budget is required")
    private Double totalBudget;

    @NotNull(message = "Max rewards per day is required")
    private Integer maxRewardsPerDay;

    private Set<String> tags;

    @NotNull(message = "Platform ID is required")
    private Long platformId;
}
