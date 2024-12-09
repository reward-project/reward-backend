package com.outsider.reward.domain.store.command.application;

import com.outsider.reward.domain.store.command.domain.StoreMission;
import com.outsider.reward.domain.store.command.domain.StoreMissionRepository;
import com.outsider.reward.domain.store.command.domain.service.StoreMissionDomainService;
import com.outsider.reward.domain.store.command.dto.CreateStoreMissionRequest;
import com.outsider.reward.domain.store.command.dto.StoreMissionResponse;
import com.outsider.reward.domain.store.command.mapper.StoreMissionMapper;
import com.outsider.reward.domain.platform.command.domain.PlatformRepository;
import com.outsider.reward.domain.tag.command.domain.TagRepository;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.domain.member.exception.MemberErrorCode;
import com.outsider.reward.domain.member.exception.MemberException;
import com.outsider.reward.domain.member.command.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreMissionCommandServiceImpl implements StoreMissionCommandService {

    private final StoreMissionRepository storeMissionRepository;
    private final StoreMissionDomainService storeMissionDomainService;
    private final StoreMissionMapper storeMissionMapper;
    private final PlatformRepository platformRepository;
    private final TagRepository tagRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public StoreMissionResponse createStoreMission(CreateStoreMissionRequest request) {
        Member registrant = memberRepository.findById(request.getRegistrantId())
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        
        StoreMission storeMission = storeMissionMapper.toEntity(request, platformRepository, tagRepository, registrant);
        storeMissionDomainService.validateStoreMission(storeMission);
        StoreMission savedMission = storeMissionRepository.save(storeMission);
        return storeMissionMapper.toResponse(savedMission);
    }
}
