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
import com.outsider.reward.domain.finance.command.domain.Account;
import com.outsider.reward.domain.finance.command.domain.AccountRepository;
import com.outsider.reward.domain.finance.command.domain.Transaction;
import com.outsider.reward.domain.finance.command.domain.TransactionType;
import com.outsider.reward.domain.finance.command.domain.TransactionRepository;
import com.outsider.reward.domain.finance.exception.AccountException;
import com.outsider.reward.domain.finance.exception.AccountErrorCode;
import com.outsider.reward.domain.member.command.domain.Member;
import java.time.temporal.ChronoUnit;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreMissionDomainService {

    private final StoreMissionRepository storeMissionRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final StoreMissionMapper storeMissionMapper;
    private final PlatformRepository platformRepository;
    private final TagRepository tagRepository;
    private final MemberRepository memberRepository;

    public void validateStoreMission(StoreMission storeMission) {
        validateDates(storeMission);
        validateProductLink(storeMission);
    }

    @Transactional
    public StoreMission createStoreMission(CreateStoreMissionRequest request) {
        // 1. 총 예산 계산
        double totalBudget = calculateTotalBudget(request);

        // 2. 등록자의 계정 조회 및 잔액 확인
        Member registrant = memberRepository.findById(request.getRegistrantId())
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Account account = accountRepository.findByMember(registrant)
            .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        if (account.getBalance() < totalBudget) {
            throw new AccountException(AccountErrorCode.INSUFFICIENT_BALANCE);
        }

        // 3. 리워드 미션 생성
        StoreMission mission = storeMissionMapper.toEntity(request, platformRepository, tagRepository, registrant);
        mission.initializeBudget(totalBudget);

        // 4. 등록자 계정에서 예산 차감
        Transaction transaction = Transaction.builder()
            .account(account)
            .amount(-totalBudget)
            .type(TransactionType.REWARD_BUDGET)
            .description("리워드 미션 예산 설정: " + mission.getRewardName())
            .build();

        account.addTransaction(transaction);
        transactionRepository.save(transaction);

        return storeMissionRepository.save(mission);
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

    private double calculateTotalBudget(CreateStoreMissionRequest request) {
        int durationInDays = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        return request.getRewardAmount() * request.getMaxRewardsPerDay() * durationInDays;
    }

    @Transactional
    public void refundMissionBudget(StoreMission mission) {
        double refundAmount = mission.refundRemainingBudget();
        if (refundAmount <= 0) {
            return;
        }

        Account registrantAccount = accountRepository.findByMember(mission.getRegistrant())
            .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        Transaction refundTransaction = Transaction.builder()
            .account(registrantAccount)
            .amount(refundAmount)
            .type(TransactionType.REWARD_REFUND)
            .description("리워드 미션 잔액 환불: " + mission.getRewardName())
            .build();

        registrantAccount.addTransaction(refundTransaction);
        transactionRepository.save(refundTransaction);
    }
}
