package com.outsider.reward.domain.store.command.dto;

import com.outsider.reward.domain.store.command.domain.Platform;
import com.outsider.reward.domain.store.command.validator.ValidDateRange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@ValidDateRange
public class CreateStoreMissionRequest {
    
    @NotBlank(message = "Reward name is required")
    private String rewardName;

    @NotBlank(message = "Platform is required")
    private String platform;

    @NotBlank(message = "Store name is required")
    private String storeName;

    @NotBlank(message = "Registrant name is required")
    private String registrantName;

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

    @NotBlank(message = "Registrant ID is required")
    private String registrantId;

    @Builder
    public CreateStoreMissionRequest(String rewardName, String platform, String storeName,
                                   String registrantName, String productLink, String keyword,
                                   String productId, String optionId, LocalDate startDate,
                                   LocalDate endDate, String registrantId) {
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
    }
}
