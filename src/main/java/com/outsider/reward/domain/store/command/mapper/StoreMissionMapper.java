package com.outsider.reward.domain.store.command.mapper;

import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.platform.command.domain.Platform;
import com.outsider.reward.domain.store.command.domain.RewardBudget;
import com.outsider.reward.domain.store.command.domain.StoreMission;
import com.outsider.reward.domain.store.command.dto.CreateStoreMissionRequest;
import com.outsider.reward.domain.store.command.dto.StoreMissionResponse;
import com.outsider.reward.domain.store.query.dto.PlatformInfo;
import com.outsider.reward.domain.store.query.dto.RewardInfo;
import com.outsider.reward.domain.store.query.dto.StoreInfo;
import com.outsider.reward.domain.store.query.dto.StoreMissionQueryDto;
import com.outsider.reward.domain.tag.command.domain.TagRepository;
import com.outsider.reward.global.security.CustomUserDetails;
import com.outsider.reward.domain.store.query.dto.RegistrantInfo;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;

import com.outsider.reward.domain.platform.command.domain.PlatformRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import com.outsider.reward.domain.tag.command.domain.Tag;

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
            .optionId(request.getOptionId())
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

    @Mapping(source = "platform", target = "platform", qualifiedByName = "toPlatformInfo")
    @Mapping(source = "rewardName", target = "reward.rewardName")
    @Mapping(source = "rewardId", target = "reward.rewardId")
    @Mapping(source = "rewardAmount", target = "reward.rewardAmount")
    @Mapping(source = "maxRewardsPerDay", target = "reward.maxRewardsPerDay")
    @Mapping(source = "startDate", target = "reward.startDate")
    @Mapping(source = "endDate", target = "reward.endDate")
    @Mapping(source = "storeName", target = "store.storeName")
    @Mapping(source = "productLink", target = "store.productLink")
    @Mapping(source = "keyword", target = "store.keyword")
    @Mapping(source = "productId", target = "store.productId")
    @Mapping(source = "optionId", target = "store.optionId")
    @Mapping(source = "registrant", target = "registrant", qualifiedByName = "toRegistrantInfo")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
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
            .optionId(mission.getOptionId())
            .build();
    }

    @Named("toRegistrantInfo")
    default RegistrantInfo toRegistrantInfo(Member registrant) {
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
}
