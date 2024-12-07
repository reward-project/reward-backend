package com.outsider.reward.domain.store.command.domain.service;

import com.outsider.reward.domain.store.command.domain.StoreMission;
import com.outsider.reward.domain.store.exception.StoreMissionException;
import com.outsider.reward.domain.store.exception.StoreMissionErrorCode;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outsider.reward.domain.store.command.domain.StoreMissionRepository;
import com.outsider.reward.domain.store.command.dto.CreateStoreMissionRequest;
import com.outsider.reward.domain.store.command.mapper.StoreMissionMapper;
import com.outsider.reward.domain.platform.command.domain.PlatformRepository;
import com.outsider.reward.domain.tag.command.application.TagService;
import com.outsider.reward.domain.tag.command.domain.TagRepository;
import java.time.LocalDate;
import java.util.ArrayList;

import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.domain.member.exception.MemberException;
import com.outsider.reward.domain.member.exception.MemberErrorCode;
import com.outsider.reward.domain.member.command.domain.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreMissionDomainService {

    private final StoreMissionRepository storeMissionRepository;
    private final PlatformRepository platformRepository;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final MemberRepository memberRepository;

    public void validateStoreMission(StoreMission storeMission) {
        validateDates(storeMission);
        validateProductLink(storeMission);
    }

    @Transactional
    public StoreMission createStoreMission(CreateStoreMissionRequest request) {
        Member currentMember = memberRepository.findById(request.getRegistrantId())
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        StoreMission storeMission = StoreMissionMapper.INSTANCE.toEntity(
            request, 
            platformRepository,
            tagRepository,
            currentMember
        );
        return storeMissionRepository.save(storeMission);
    }

    private void validateDates(StoreMission storeMission) {
        if (storeMission.getEndDate().isBefore(storeMission.getStartDate())) {
            throw new StoreMissionException(StoreMissionErrorCode.INVALID_DATE_RANGE);
        }
        
        LocalDate today = LocalDate.now();
        if (storeMission.getStartDate().isBefore(today)) {
            throw new StoreMissionException(StoreMissionErrorCode.PAST_START_DATE);
        }
    }

    private void validateProductLink(StoreMission storeMission) {
        String productLink = storeMission.getProductLink();
        try {
            if (!java.net.URI.create(productLink).isAbsolute()) {
                throw new StoreMissionException(StoreMissionErrorCode.INVALID_PRODUCT_LINK);
            }
        } catch (IllegalArgumentException e) {
            throw new StoreMissionException(StoreMissionErrorCode.INVALID_PRODUCT_LINK, e);
        }
    }
}
