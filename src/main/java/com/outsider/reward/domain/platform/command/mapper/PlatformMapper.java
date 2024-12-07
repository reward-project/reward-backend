package com.outsider.reward.domain.platform.command.mapper;

import com.outsider.reward.domain.platform.command.domain.Platform;
import com.outsider.reward.domain.platform.command.domain.PlatformDomain;
import com.outsider.reward.domain.platform.command.domain.PlatformDomainStatus;
import com.outsider.reward.domain.platform.command.dto.AddPlatformDomainRequest;
import com.outsider.reward.domain.platform.command.dto.CreatePlatformRequest;
import com.outsider.reward.domain.platform.command.dto.PlatformResponse;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PlatformMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "domains", ignore = true)
    @BeanMapping(builder = @Builder(disableBuilder = true))
    Platform toEntity(CreatePlatformRequest request);

    @AfterMapping
    default void afterToEntity(@MappingTarget Platform platform, CreatePlatformRequest request) {
        platform.setCreatedAt(LocalDateTime.now());
    }

    @Named("mapDomains")
    default List<String> mapDomains(List<PlatformDomain> domains) {
        return domains.stream()
            .map(PlatformDomain::getDomain)
            .toList();
    }

    @Mapping(source = "domains", target = "domains", qualifiedByName = "mapDomains")
    PlatformResponse toResponse(Platform platform);
}

