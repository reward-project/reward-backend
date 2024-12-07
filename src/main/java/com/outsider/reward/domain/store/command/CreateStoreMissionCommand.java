package com.outsider.reward.domain.store.command;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Builder
public class CreateStoreMissionCommand {
    private final String rewardName;
    private final Long platformId;
    private final String storeName;
    private final String registrantName;
    private final String productLink;
    private final String keyword;
    private final String productId;
    private final String optionId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String registrantId;
    private final String rewardId;
    private final String platformName;
    private final Double rewardAmount;
    private final Integer maxRewardsPerDay;
    private final List<String> tags;
}
