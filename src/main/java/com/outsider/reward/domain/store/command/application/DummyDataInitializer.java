package com.outsider.reward.domain.store.command.application;

import com.outsider.reward.domain.member.command.application.MemberCommandService;
import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.domain.platform.command.application.PlatformService;
import com.outsider.reward.domain.platform.command.domain.Platform;
import com.outsider.reward.domain.platform.command.domain.PlatformDomain;
import com.outsider.reward.domain.platform.command.domain.PlatformDomainRepository;
import com.outsider.reward.domain.platform.command.domain.PlatformDomainStatus;
import com.outsider.reward.domain.platform.command.domain.PlatformRepository;
import com.outsider.reward.domain.platform.command.domain.PlatformStatus;
import com.outsider.reward.domain.platform.command.dto.CreatePlatformRequest;
import com.outsider.reward.domain.platform.command.dto.PlatformResponse;
import com.outsider.reward.domain.store.command.domain.StoreMissionRepository;
import com.outsider.reward.domain.store.command.dto.CreateStoreMissionRequest;
import com.outsider.reward.domain.store.command.dto.StoreMissionResponse;
import com.outsider.reward.domain.tag.command.domain.Tag;
import com.outsider.reward.domain.tag.command.domain.TagRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DummyDataInitializer {

    private final MemberRepository memberRepository;
    private final PlatformRepository platformRepository;
    private final PlatformDomainRepository platformDomainRepository;
    private final TagRepository tagRepository;
    private final StoreMissionRepository storeMissionRepository;
    private final StoreMissionCommandService storeMissionCommandService;
    private final PlatformService platformService;
    private final MissionCompletionService missionCompletionService;
    private final MemberCommandService memberCommandService;

    @PostConstruct
    @Transactional
    public void initDummyData() {
        if (isDataAlreadyExists()) {
            log.info("Dummy data already exists. Skipping initialization.");
            return;
        }

        Member member = createMember();
        Platform platform = createPlatform();
        Tag tag = createTag(member);
        StoreMissionResponse mission = createStoreMission(member, platform, tag);
        
        // Complete the mission with productId as answer
            missionCompletionService.completeMission(
                member.getId(),
                mission.getId(),
                "PROD123"  // productId를 답변으로 사용
            );
            log.info("Mission completed successfully for member: {}", member.getId());
   
        
    }

    private boolean isDataAlreadyExists() {
        return memberRepository.count() > 0 || 
               platformRepository.count() > 0 || 
               tagRepository.count() > 0 || 
               storeMissionRepository.count() > 0;
    }

    private Member createMember() {
        return memberCommandService.createOAuthMember(
            "dudnjsckrgo@gmail.com",
            "윤여원",
            "google",
            "platform",  // platform value
            "user"      // default role
        );
    }

    private Platform createPlatform() {
        CreatePlatformRequest request = CreatePlatformRequest.builder()
            .name("naver")
            .displayName("네이버")
            .description("네이버 플랫폼")
            .domains(List.of("example.com"))
            .build();

        PlatformResponse response = platformService.createPlatform(request);
        Platform platform = platformRepository.findById(response.getId())
            .orElseThrow(() -> new IllegalStateException("Platform not found"));
        
        // Approve platform and its domains
        platformService.approvePlatform(platform.getId());
        
        return platform;
    }

    private Tag createTag(Member member) {
        Tag tag = Tag.createPrivate("식품", member);
        return tagRepository.save(tag);
    }

    private StoreMissionResponse createStoreMission(Member member, Platform platform, Tag tag) {
        CreateStoreMissionRequest request = CreateStoreMissionRequest.builder()
            .rewardName("테스트 리워드")
            .storeName("테스트 스토어")
            .productLink("https://example.com/product")
            .keyword("테스트")
            .productId("PROD123")
            .optionId("OPT123")
            .rewardAmount(1000.0)
            .totalBudget(10000.0)
            .maxRewardsPerDay(100)
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(7))
            .registrantId(member.getId())
            .platformId(platform.getId())
            .tags(new HashSet<>(Arrays.asList(tag.getName())))
            .build();

        return storeMissionCommandService.createStoreMission(request);
    }
}
