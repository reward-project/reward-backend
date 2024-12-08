package com.outsider.reward.domain.store.command.application;

import com.outsider.reward.domain.store.command.dto.UseRewardRequest;
import com.outsider.reward.domain.store.command.domain.RewardUsage;
import com.outsider.reward.domain.store.command.domain.StoreMission;
import com.outsider.reward.domain.store.command.domain.RewardUsageRepository;
import com.outsider.reward.domain.store.command.domain.StoreMissionRepository;
import com.outsider.reward.domain.store.exception.StoreMissionException;
import com.outsider.reward.domain.store.exception.StoreMissionErrorCode;
import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.domain.member.exception.MemberException;
import com.outsider.reward.domain.member.exception.MemberErrorCode;
import com.outsider.reward.domain.finance.command.domain.Account;
import com.outsider.reward.domain.finance.command.domain.Transaction;
import com.outsider.reward.domain.finance.command.domain.TransactionType;
import com.outsider.reward.domain.finance.command.domain.AccountRepository;
import com.outsider.reward.domain.finance.command.domain.TransactionRepository;
import com.outsider.reward.domain.finance.exception.AccountException;
import com.outsider.reward.domain.finance.exception.AccountErrorCode;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RewardUsageService {
    private final StoreMissionRepository storeMissionRepository;
    private final MemberRepository memberRepository;
    private final RewardUsageRepository rewardUsageRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void useReward(UseRewardRequest request) {
        // 1. 리워드 미션 조회
        StoreMission mission = storeMissionRepository.findById(request.getStoreMissionId())
            .orElseThrow(() -> new StoreMissionException(StoreMissionErrorCode.STORE_MISSION_NOT_FOUND));

        // 2. 사용자 조회
        Member user = memberRepository.findById(request.getUserId())
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 3. 사용자가 이미 리워드를 사용했는지 확인
        RewardUsage.validateUserCanUseReward(user, mission);

        // 4. 리워드 사용 가능 여부 확인
        if (!mission.canUseReward(request.getAmount())) {
            throw new StoreMissionException(StoreMissionErrorCode.REWARD_USAGE_EXCEEDED);
        }

        // 5. 리워드 사용 기록 생성
        RewardUsage usage = new RewardUsage(mission, user, request.getAmount());
        rewardUsageRepository.save(usage);

        // 6. 사용자 계정에 리워드 금액 추가
        Account account = accountRepository.findByMember(user)
            .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        Transaction transaction = Transaction.builder()
            .account(account)
            .amount(request.getAmount())
            .type(TransactionType.REWARD)
            .description("리워드 적립: " + mission.getRewardName())
            .rewardUsage(usage)
            .build();

        account.addTransaction(transaction);
        transactionRepository.save(transaction);

        // 7. 리워드 예산 차감
        mission.useReward(request.getAmount());
        storeMissionRepository.save(mission);
    }
} 