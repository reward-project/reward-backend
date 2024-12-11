package com.outsider.reward.domain.store.command.application;

import com.outsider.reward.domain.store.command.domain.StoreMission;
import com.outsider.reward.domain.store.command.domain.StoreMissionRepository;
import com.outsider.reward.domain.store.command.domain.service.StoreMissionDomainService;
import com.outsider.reward.domain.store.command.dto.CreateStoreMissionRequest;
import com.outsider.reward.domain.store.command.dto.StoreMissionResponse;
import com.outsider.reward.domain.store.command.dto.UpdateStoreMissionRequest;
import com.outsider.reward.domain.store.command.mapper.StoreMissionMapper;
import com.outsider.reward.domain.platform.command.domain.Platform;
import com.outsider.reward.domain.platform.command.domain.PlatformRepository;
import com.outsider.reward.domain.tag.command.domain.Tag;
import com.outsider.reward.domain.tag.command.domain.TagRepository;

import jakarta.persistence.EntityNotFoundException;

import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.domain.member.exception.MemberErrorCode;
import com.outsider.reward.domain.member.exception.MemberException;
import com.outsider.reward.domain.member.command.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        StoreMission savedMission = storeMissionDomainService.createStoreMission(request);
        return storeMissionMapper.toResponse(savedMission);
    }

    @Override
    @Transactional
    public StoreMissionResponse updateStoreMission(Long id, UpdateStoreMissionRequest request, Long userId) {
        StoreMission mission = storeMissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Store mission not found with id: " + id));

        // 권한 체크
        if (!mission.getRegistrantId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to update this mission");
        }

        // 멤버 조회
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + userId));

        // 플랫폼 조회
        Platform platform = platformRepository.findById(request.getPlatformId())
                .orElseThrow(() -> new EntityNotFoundException("Platform not found with id: " + request.getPlatformId()));

        // 미션 업데이트
        int durationInDays = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        double totalBudget = request.getRewardAmount().doubleValue() * request.getMaxRewardsPerDay().intValue() * durationInDays;
        
        // 기존 사용량 체크
        double usedBudget = mission.getTotalRewardUsage();
        if (totalBudget < usedBudget) {
            throw new IllegalArgumentException(
                String.format("새로운 총 예산(%f)이 이미 사용된 예산(%f)보다 작을 수 없습니다", 
                    totalBudget, usedBudget)
            );
        }
        
        mission.updateReward(
                request.getRewardName(),
                request.getRewardAmount().doubleValue(),
                request.getMaxRewardsPerDay().intValue(),
                request.getStartDate(),
                request.getEndDate()
        );

        mission.updateStore(
                request.getStoreName(),
                request.getProductLink(),
                request.getKeyword(),
                request.getProductId()
        );

        mission.updatePlatform(platform);

        // 태그 업데이트
        Set<Tag> tags = new HashSet<>();
        for (String tagName : request.getTags()) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> Tag.createPublic(tagName, member));
            tag.incrementUseCount();
            tags.add(tagRepository.save(tag));
        }
        mission.updateTags(tags);

        return storeMissionMapper.toResponse(mission);
    }

    @Override
    @Transactional
    public void deleteStoreMission(Long id, Long userId) {
        StoreMission mission = storeMissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Store mission not found with id: " + id));

        // 권한 체크
        if (!mission.getRegistrantId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to delete this mission");
        }

        storeMissionRepository.delete(mission);
    }

    @Override
    public StoreMission getStoreMissionById(Long id) {
        return storeMissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Store Mission not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public StoreMission getStoreMissionForEdit(Long id, Long userId) {
        StoreMission mission = storeMissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Store Mission not found with id: " + id));
        
        if (!mission.getRegistrantId().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to edit this mission");
        }
        
        storeMissionDomainService.validateStoreMission(mission);
        return mission;
    }
}
