package com.outsider.reward.domain.store.query.mapper;

import com.outsider.reward.domain.store.command.domain.StoreMission;
import com.outsider.reward.domain.store.query.dto.StoreMissionQueryDto;
import com.outsider.reward.domain.tag.command.domain.Tag;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StoreMissionQueryMapper {

    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagsToStrings")
    StoreMissionQueryDto toDto(StoreMission storeMission);

    @Named("tagsToStrings")
    default Set<String> tagsToStrings(Set<Tag> tags) {
        if (tags == null) return new HashSet<>();
        return tags.stream()
            .map(Tag::getName)
            .collect(Collectors.toSet());
    }
}
