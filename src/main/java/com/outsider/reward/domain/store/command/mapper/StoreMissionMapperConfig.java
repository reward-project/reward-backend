package com.outsider.reward.domain.store.command.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.outsider.reward.domain.platform.command.domain.PlatformRepository;
import org.mapstruct.factory.Mappers;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import org.mapstruct.Context;
import com.outsider.reward.domain.store.command.domain.StoreMission;
import com.outsider.reward.domain.store.command.dto.CreateStoreMissionRequest;
import com.outsider.reward.domain.store.command.dto.StoreMissionResponse;
import com.outsider.reward.domain.tag.command.domain.Tag;
import com.outsider.reward.domain.tag.command.domain.TagRepository;
import com.outsider.reward.global.security.CustomUserDetails;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.domain.member.command.domain.Member;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import org.mapstruct.Named;
import org.mapstruct.Mapper;

@Configuration
public abstract class StoreMissionMapperConfig {

    @Autowired
    protected PlatformRepository platformRepository;

    @Autowired
    protected TagRepository tagRepository;

    @Autowired
    protected MemberRepository memberRepository;

    @Bean
    public StoreMissionMapper storeMissionMapper() {
        return Mappers.getMapper(StoreMissionMapper.class);
    }

    @Named("tagsToTagEntities")
    protected Set<Tag> mapTags(Set<String> tagNames, @Context TagRepository tagRepository, @Context SecurityContext securityContext) {
        if (tagNames == null || tagNames.isEmpty()) return new HashSet<>();
        
        // 현재 사용자 가져오기
        Member currentMember = null;
        if (securityContext != null && securityContext.getAuthentication() != null) {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                currentMember = userDetails.getMember();
            }
        }

        if (currentMember == null) {
            throw new IllegalStateException("User must be authenticated to create tags");
        }

        final Member member = currentMember;  // effectively final for lambda
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
                        Tag newTag = new Tag(trimmedName, false, member);  // 비공개 태그로 생성하고 현재 사용자를 createdBy로 설정
                        return tagRepository.save(newTag);
                    });
            })
            .collect(Collectors.toSet());
    }
} 
