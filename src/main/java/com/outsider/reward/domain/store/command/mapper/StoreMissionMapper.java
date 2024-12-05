package com.outsider.reward.domain.store.command.mapper;

import com.outsider.reward.domain.store.command.domain.Platform;
import com.outsider.reward.domain.store.command.domain.StoreMission;
import com.outsider.reward.domain.store.command.dto.CreateStoreMissionRequest;
import com.outsider.reward.domain.store.command.dto.StoreMissionResponse;
import com.outsider.reward.domain.store.query.dto.StoreMissionQueryDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface StoreMissionMapper {

    StoreMissionMapper INSTANCE = Mappers.getMapper(StoreMissionMapper.class);

    @Mapping(source = "platform", target = "platform", qualifiedByName = "stringToPlatform")
    StoreMission toEntity(CreateStoreMissionRequest request);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "platform", target = "platform")
    StoreMissionResponse toResponse(StoreMission storeMission);

    @Named("stringToPlatform")
    default Platform stringToPlatform(String platform) {
        return Platform.valueOf(platform);
    }

    StoreMissionQueryDto toQueryDto(StoreMission storeMission);

    @Mapping(source = "userId", target = "registrantId")
    CreateStoreMissionRequest toRequestWithUserId(CreateStoreMissionRequest request, String userId);
}
