package com.outsider.reward.domain.store.command.application;

import com.outsider.reward.domain.finance.command.application.BankTransferService;
import com.outsider.reward.domain.finance.command.application.NaverPayService;
import com.outsider.reward.domain.finance.command.application.RewardSettlementService;
import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.domain.platform.command.domain.Platform;
import com.outsider.reward.domain.platform.command.domain.PlatformRepository;
import com.outsider.reward.domain.platform.command.domain.PlatformStatus;
import com.outsider.reward.domain.tag.command.domain.Tag;
import com.outsider.reward.domain.tag.command.domain.TagRepository;
import com.outsider.reward.domain.store.command.dto.CreateStoreMissionRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

@SpringBootTest(classes = {TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class StoreMissionDummyDataTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private TagRepository tagRepository;

    @MockBean
    private NaverPayService naverPayService;

    @MockBean
    private BankTransferService bankTransferService;

    @MockBean
    private RewardSettlementService rewardSettlementService;

    @Autowired
    private StoreMissionCommandService storeMissionCommandService;

    @Nested
    @DisplayName("스토어 미션 생성 테스트")
    class CreateStoreMission {
        
        @Test
        @DisplayName("1단계: 멤버 생성")
        void step1_createMember() {
            // Given & When
            Member member = Member.createOAuthMember(
                "test@example.com",
                "Test User",
                "Tester",
                "google"
            );
            memberRepository.save(member);

            // Then
            assertThat(member.getId()).isNotNull();
            assertThat(member.getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("2단계: 플랫폼 생성")
        void step2_createPlatform() {
            // Given & When
            Platform platform = Platform.builder()
                    .name("COUPANG")
                    .displayName("쿠팡")
                    .description("쿠팡 이커머스 플랫폼")
                    .build();
            platform.approve();
            platform.addDomain("coupang.com");
            platformRepository.save(platform);

            // Then
            assertThat(platform.getId()).isNotNull();
            assertThat(platform.getName()).isEqualTo("COUPANG");
            assertThat(platform.getStatus()).isEqualTo(PlatformStatus.ACTIVE);
            assertThat(platform.getDomains()).hasSize(1);
            assertThat(platform.getDomains().get(0).getDomain()).isEqualTo("coupang.com");
        }

        @Test
        @DisplayName("3단계: 태그 생성")
        void  step3_createTag() {
            // Given
            Member member = Member.createOAuthMember(
                "test@example.com",
                "Test User",
                "Tester",
                "google"
            );
            memberRepository.save(member);

            // When
            Tag tag = Tag.createPrivate("Test Tag", member);
            tagRepository.save(tag);

            // Then
            assertThat(tag.getId()).isNotNull();
            assertThat(tag.getName()).isEqualTo("Test Tag");
            assertThat(tag.getCreatedBy()).isEqualTo(member);
        }

        @Test
        @DisplayName("4단계: 스토어 미션 생성")
        void step4_createStoreMission() {
            // Given
            Member member = Member.createOAuthMember(
                "test@example.com",
                "Test User",
                "Tester",
                "google"
            );
            memberRepository.save(member);

            Platform platform = Platform.builder()
                    .name("COUPANG")
                    .displayName("쿠팡")
                    .description("쿠팡 이커머스 플랫폼")
                    .build();
            platform.approve();
            platform.addDomain("coupang.com");
            platformRepository.save(platform);

            // When
            CreateStoreMissionRequest request = CreateStoreMissionRequest.builder()
                .registrantId(member.getId())
                .platformId(platform.getId())
                .rewardName("쿠팡 상품 리뷰")
                .rewardAmount(10000.0)
                .storeName("쿠팡 공식스토어")
                .productLink("https://www.coupang.com/products/123")
                .keyword("생수")
                .productId("product-123")
                .optionId("option-123")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .totalBudget(100000.0)
                .maxRewardsPerDay(10)
                .tags(new HashSet<>(Arrays.asList("식품", "생활용품")))
                .build();

            var response = storeMissionCommandService.createStoreMission(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isNotNull();
            assertThat(response.getReward().getRewardName()).isEqualTo("쿠팡 상품 리뷰");
            assertThat(response.getStore().getStoreName()).isEqualTo("쿠팡 공식스토어");
            assertThat(response.getPlatform().getName()).isEqualTo("COUPANG");
            assertThat(response.getRegistrant().getRegistrantId()).isEqualTo(member.getId());
            assertThat(response.getTotalUsageCount()).isZero();
            assertThat(response.getTodayUsageCount()).isZero();
        }
    }
}
