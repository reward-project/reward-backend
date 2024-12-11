package com.outsider.reward.domain.store.command.mapper;

import com.outsider.reward.domain.finance.command.domain.RewardBudget;
import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.RoleType;
import com.outsider.reward.domain.platform.command.domain.Platform;
import com.outsider.reward.domain.platform.command.domain.PlatformRepository;
import com.outsider.reward.domain.store.command.domain.StoreMission;
import com.outsider.reward.domain.store.command.dto.CreateStoreMissionRequest;
import com.outsider.reward.domain.store.command.dto.StoreMissionResponse;
import com.outsider.reward.domain.store.query.dto.PlatformInfo;
import com.outsider.reward.domain.store.query.dto.RegistrantInfo;
import com.outsider.reward.domain.store.query.dto.RewardInfo;
import com.outsider.reward.domain.store.query.dto.StoreInfo;
import com.outsider.reward.domain.store.query.dto.StoreMissionQueryDto;
import com.outsider.reward.domain.tag.command.domain.Tag;
import com.outsider.reward.domain.tag.command.domain.TagRepository;
import com.outsider.reward.global.security.CustomUserDetails;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
    componentModel = "spring",
    uses = {
        PlatformRepository.class,
        TagRepository.class,
        SecurityContext.class
    }
)
public interface StoreMissionMapper {

    StoreMissionMapper INSTANCE = Mappers.getMapper(StoreMissionMapper.class);

    @Named("platformIdToPlatform")
    default Platform platformIdToPlatform(Long platformId, @Context PlatformRepository platformRepository) {
        if (platformId == null) return null;
        return platformRepository.findById(platformId)
            .orElseThrow(() -> new IllegalArgumentException("Platform not found with id: " + platformId));
    }

    @Mapping(source = "platformId", target = "platform", qualifiedByName = "platformIdToPlatform")
    @Mapping(source = "tags", target = "tags", qualifiedByName = "tagsToTagEntities")
    default StoreMission toEntity(CreateStoreMissionRequest request, 
                         @Context PlatformRepository platformRepository, 
                         @Context TagRepository tagRepository,
                         @Context Member registrant) {
        Platform platform = platformIdToPlatform(request.getPlatformId(), platformRepository);
        Set<Tag> tags = mapTags(request.getTags(), tagRepository, registrant);
        
        StoreMission storeMission = StoreMission.builder()
            .rewardName(request.getRewardName())
            .platform(platform)
            .storeName(request.getStoreName())
            .registrant(registrant)
            .productLink(request.getProductLink())
            .keyword(request.getKeyword())
            .productId(request.getProductId())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .rewardAmount(request.getRewardAmount())
            .maxRewardsPerDay(request.getMaxRewardsPerDay())
            .tags(tags)
            .build();

        // RewardBudget 초기화
        storeMission.initializeBudget(request.getTotalBudget());
        
        return storeMission;
    }

    @Mapping(target = "platform", source = "platform")
    @Mapping(target = "reward.rewardName", source = "rewardName")
    @Mapping(target = "reward.rewardAmount", source = "rewardAmount")
    @Mapping(target = "reward.maxRewardsPerDay", source = "maxRewardsPerDay")
    @Mapping(target = "reward.startDate", source = "startDate")
    @Mapping(target = "reward.endDate", source = "endDate")
    @Mapping(target = "store.storeName", source = "storeName")
    @Mapping(target = "store.productLink", source = "productLink")
    @Mapping(target = "store.keyword", source = "keyword")
    @Mapping(target = "store.productId", source = "productId")
    @Mapping(target = "registrant", source = "registrant", qualifiedByName = "toRegistrantInfo")
    @Mapping(target = "totalUsageCount", expression = "java(storeMission.getBudget().getUsedRewardsToday())")
    @Mapping(target = "todayUsageCount", source = "budget.usedRewardsToday")
    @Mapping(target = "usageRate", expression = "java(storeMission.getBudget().getUsedBudget() / storeMission.getBudget().getTotalBudget())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "usageByHour", ignore = true)
    @Mapping(target = "usageByDay", ignore = true)
    @Mapping(target = "recentUsages", ignore = true)
    StoreMissionResponse toResponse(StoreMission storeMission);

    @Named("toPlatformInfo")
    default PlatformInfo toPlatformInfo(Platform platform) {
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
        return RewardInfo.builder()
            .rewardId(mission.getRewardId())
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
        return StoreInfo.builder()
            .storeName(mission.getStoreName())
            .productLink(mission.getProductLink())
            .keyword(mission.getKeyword())
            .productId(mission.getProductId())
            .build();
    }

    @Named("toRegistrantInfo")
    default RegistrantInfo toRegistrantInfo(Member registrant) {
        if (registrant == null) {
            return null;
        }
        return RegistrantInfo.builder()
                .registrantId(registrant.getId())
                .registrantName(registrant.getBasicInfo().getName())
                .registrantEmail(registrant.getBasicInfo().getEmail())
                .registrantRole(mapRegistrantRoles(registrant))
                .build();
    }

    @Named("calculateRewardStatus")
    default String calculateRewardStatus(StoreMission mission) {
        LocalDate now = LocalDate.now();
        if (now.isBefore(mission.getStartDate())) return "SCHEDULED";
        if (now.isAfter(mission.getEndDate())) return "ENDED";
        return "ACTIVE";
    }

    @Mapping(source = "request.rewardName", target = "rewardName")
    @Mapping(source = "userId", target = "registrantId")
    CreateStoreMissionRequest toRequestWithUserId(CreateStoreMissionRequest request, Long userId);

    @Named("tagsToTagEntities")
    default Set<Tag> mapTags(Set<String> tagNames, 
                            @Context TagRepository tagRepository,
                            @Context Member currentMember) {
        if (tagNames == null || tagNames.isEmpty()) return new HashSet<>();
        
        if (currentMember == null) {
            throw new IllegalStateException("User must be authenticated to create tags");
        }

        return tagNames.stream()
            .filter(name -> name != null && !name.trim().isEmpty())
            .map(name -> {
                String trimmedName = name.trim();
                return tagRepository.findByName(trimmedName)
                    .map(existingTag -> {
                        existingTag.incrementUseCount();
                        return tagRepository.save(existingTag);
                    })
                    .orElseGet(() -> {
                        Tag newTag = Tag.createPrivate(trimmedName, currentMember);
                        return tagRepository.save(newTag);
                    });
            })
            .collect(Collectors.toSet());
    }

    @Named("mapRegistrantRoles")
    default String mapRegistrantRoles(Member registrant) {
        if (registrant == null || registrant.getRoles() == null || registrant.getRoles().isEmpty()) {
            return "";
        }
        return registrant.getRoles().stream()
                .map(RoleType::name)
                .collect(Collectors.joining(","));
    }
}
