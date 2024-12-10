package com.outsider.reward.domain.store.query.mapper;

import com.outsider.reward.domain.store.command.domain.RewardUsage;
import com.outsider.reward.domain.store.command.domain.RewardUsageStatus;
import com.outsider.reward.domain.store.command.domain.StoreMission;
import com.outsider.reward.domain.store.query.dto.StoreMissionQueryDto;
import com.outsider.reward.domain.store.query.dto.StoreMissionQueryResponse;
import com.outsider.reward.domain.store.query.dto.PlatformInfo;
import com.outsider.reward.domain.store.query.dto.RewardInfo;
import com.outsider.reward.domain.store.query.dto.StoreInfo;
import com.outsider.reward.domain.store.query.dto.RegistrantInfo;
import com.outsider.reward.domain.tag.command.domain.Tag;
import com.outsider.reward.domain.platform.command.domain.Platform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StoreMissionQueryMapper {

    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagsToStrings")
    @Mapping(target = "totalRewardUsage", source = "rewardUsages", qualifiedByName = "calculateTotalRewardUsage")
    @Mapping(target = "remainingRewardBudget", source = "budget.remainingBudget")
    @Mapping(target = "platform", source = "platform", qualifiedByName = "toPlatformInfo")
    @Mapping(target = "reward", source = ".", qualifiedByName = "toRewardInfo")
    @Mapping(target = "store", source = ".", qualifiedByName = "toStoreInfo")
    @Mapping(target = "registrant", source = "registrant", qualifiedByName = "toRegistrantInfo")
    @Mapping(target = "status", expression = "java(storeMission.isExpired() ? \"ENDED\" : (java.time.LocalDate.now().isBefore(storeMission.getStartDate()) ? \"SCHEDULED\" : \"ACTIVE\"))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    StoreMissionQueryDto toDto(StoreMission storeMission);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "rewardName")
    @Mapping(target = "description", source = "keyword")
    @Mapping(target = "rewardPoint", source = "rewardAmount")
    @Mapping(target = "status", expression = "java(mission.isExpired() ? \"ENDED\" : (java.time.LocalDate.now().isBefore(mission.getStartDate()) ? \"SCHEDULED\" : \"ACTIVE\"))")
    @Mapping(target = "missionUrl", source = "productLink")
    @Mapping(target = "completed", constant = "false")
    StoreMissionQueryResponse toResponse(StoreMission mission);

    default StoreMissionQueryResponse toResponse(StoreMission mission, boolean completed) {
        StoreMissionQueryResponse response = toResponse(mission);
        return response.toBuilder()
            .completed(completed)
            .build();
    }

    @Named("tagsToStrings")
    default Set<String> tagsToStrings(Set<Tag> tags) {
        if (tags == null) return new HashSet<>();
        return tags.stream()
            .map(Tag::getName)
            .collect(Collectors.toSet());
    }

    @Named("calculateTotalRewardUsage")
    default long calculateTotalRewardUsage(Set<RewardUsage> rewardUsages) {
        if (rewardUsages == null) return 0;
        return rewardUsages.stream()
            .filter(usage -> usage.getStatus() == RewardUsageStatus.COMPLETED)
            .mapToLong(usage -> (long) usage.getAmount())
            .sum();
    }

    @Named("toPlatformInfo")
    default PlatformInfo toPlatformInfo(Platform platform) {
        if (platform == null) return null;
        return PlatformInfo.builder()
            .id(platform.getId())
            .name(platform.getName())
            .displayName(platform.getDisplayName())
            .status(platform.getStatus().name())
            .createdAt(platform.getCreatedAt())
            .updatedAt(platform.getUpdatedAt())
            .build();
    }

    @Named("toRewardInfo")
    default RewardInfo toRewardInfo(StoreMission mission) {
        if (mission == null) return null;
        return RewardInfo.builder()
            .rewardName(mission.getRewardName())
            .rewardAmount(mission.getRewardAmount())
            .maxRewardsPerDay(mission.getMaxRewardsPerDay())
            .startDate(mission.getStartDate())
            .endDate(mission.getEndDate())
            .status(calculateRewardStatus(mission))
            .build();
    }

    @Named("toStoreInfo")
    default StoreInfo toStoreInfo(StoreMission mission) {
        if (mission == null) return null;
        return StoreInfo.builder()
            .storeName(mission.getStoreName())
            .productLink(mission.getProductLink())
            .keyword(mission.getKeyword())
            .productId(mission.getProductId())
            .optionId(mission.getOptionId())
            .build();
    }

    @Named("toRegistrantInfo")
    default RegistrantInfo toRegistrantInfo(com.outsider.reward.domain.member.command.domain.Member registrant) {
        if (registrant == null) return null;
        Set<String> roles = registrant.getRoleNames();
        String role = roles.isEmpty() ? "USER" : roles.iterator().next();
        return RegistrantInfo.builder()
            .registrantId(registrant.getId())
            .registrantName(registrant.getName())
            .registrantEmail(registrant.getEmail())
            .registrantRole(role)
            .build();
    }

    @Named("calculateRewardStatus")
    default String calculateRewardStatus(StoreMission mission) {
        if (mission == null) return null;
        LocalDate now = LocalDate.now();
        if (now.isBefore(mission.getStartDate())) return "SCHEDULED";
        if (now.isAfter(mission.getEndDate())) return "ENDED";
        return "ACTIVE";
    }
}
