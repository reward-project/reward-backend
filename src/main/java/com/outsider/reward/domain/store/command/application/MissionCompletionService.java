package com.outsider.reward.domain.store.command.application;

import com.outsider.reward.domain.finance.command.domain.*;
import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.domain.store.command.domain.*;
import com.outsider.reward.domain.store.command.dto.MissionCompleteRequest;
import com.outsider.reward.domain.store.command.dto.MissionCompleteResponse;
import com.outsider.reward.domain.store.exception.StoreMissionErrorCode;
import com.outsider.reward.domain.store.exception.StoreMissionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MissionCompletionService {

    private final StoreMissionRepository storeMissionRepository;
    private final MissionCompletionRepository missionCompletionRepository;
    private final RewardUsageRepository rewardUsageRepository;
    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void completeMission(Long userId, Long missionId, String answer) {
        // 1. 미션과 사용자 정보 조회
        StoreMission mission = storeMissionRepository.findById(missionId)
            .orElseThrow(() -> new StoreMissionException(StoreMissionErrorCode.STORE_MISSION_NOT_FOUND));
        
        Member user = memberRepository.findById(userId)
            .orElseThrow(() -> new StoreMissionException(StoreMissionErrorCode.USER_NOT_FOUND));
            
        Account account = accountRepository.findByMemberId(userId)
            .orElseThrow(() -> new StoreMissionException(StoreMissionErrorCode.ACCOUNT_NOT_FOUND));

        // 2. 미션 상태 검증
        validateMissionStatus(mission);

        // 3. 이미 완료한 미션인지 확인
        if (missionCompletionRepository.existsByUserIdAndMissionId(userId, missionId)) {
            throw new StoreMissionException(StoreMissionErrorCode.MISSION_ALREADY_COMPLETED);
        }

        // 4. 정답 검증
        if (!mission.validateAnswer(answer)) {
            throw new StoreMissionException(StoreMissionErrorCode.INVALID_MISSION_ANSWER);
        }

        // 5. 리워드 예산 확인 및 업데이트
        double rewardAmount = mission.getRewardAmount();
        validateAndUpdateBudget(mission, rewardAmount);

        // 6.1 미션 완료 기록
        MissionCompletion completion = MissionCompletion.builder()
            .userId(userId)
            .mission(mission)
            .build();
        missionCompletionRepository.save(completion);

        // 6.2 리워드 사용 내역 기록
        RewardUsage rewardUsage = new RewardUsage(mission, user, rewardAmount);
        rewardUsage.complete();  // 상태를 COMPLETED로 변경하고 usedAt 설정
        rewardUsageRepository.save(rewardUsage);

        // 6.3 사용자 계정 업데이트
        account.addBalance(rewardAmount);
        accountRepository.save(account);

        // 6.4 거래 내역 추가
        Transaction transaction = Transaction.builder()
            .account(account)
            .amount(rewardAmount)
            .type(TransactionType.EARN)
            .description(mission.getRewardName() + " 미션 완료 보상")
            .rewardUsage(rewardUsage)
            .build();
        transactionRepository.save(transaction);
    }

    @Transactional
    public MissionCompleteResponse completeMissionWithResponse(Long missionId, MissionCompleteRequest request) {
        completeMission(request.getUserId(), missionId, request.getMissionAnswer());
        
        StoreMission mission = storeMissionRepository.findById(missionId)
            .orElseThrow(() -> new StoreMissionException(StoreMissionErrorCode.STORE_MISSION_NOT_FOUND));
            
        return MissionCompleteResponse.builder()
            .missionId(missionId)
            .rewardPoint(mission.getRewardAmount())
            .status("COMPLETED")
            .build();
    }

    private void validateMissionStatus(StoreMission mission) {
        LocalDateTime now = LocalDateTime.now();
        
        if (now.isBefore(mission.getStartDate().atStartOfDay()) || 
            now.isAfter(mission.getEndDate().atTime(23, 59, 59))) {
            throw new StoreMissionException(StoreMissionErrorCode.MISSION_NOT_ACTIVE);
        }
    }

    private void validateAndUpdateBudget(StoreMission mission, double rewardAmount) {
        RewardBudget budget = mission.getBudget();
        if (budget == null) {
            throw new StoreMissionException(StoreMissionErrorCode.STORE_MISSION_NOT_FOUND);
        }

        if (!budget.canUseReward(rewardAmount)) {
            throw new StoreMissionException(StoreMissionErrorCode.STORE_MISSION_BUDGET_EXCEEDED);
        }

        budget.useReward(rewardAmount);
    }
}
