package com.outsider.reward.domain.finance.command.application;

import com.outsider.reward.domain.finance.command.domain.Account;
import com.outsider.reward.domain.finance.command.domain.AccountRepository;
import com.outsider.reward.domain.finance.command.domain.AccountStatus;
import com.outsider.reward.domain.finance.command.domain.RewardBudget;
import com.outsider.reward.domain.finance.command.domain.RewardBudgetRepository;
import com.outsider.reward.domain.finance.command.domain.Transaction;
import com.outsider.reward.domain.finance.command.domain.TransactionRepository;
import com.outsider.reward.domain.finance.command.domain.TransactionType;
import com.outsider.reward.domain.finance.exception.AccountException;
import com.outsider.reward.domain.finance.exception.AccountErrorCode;
import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final MemberRepository memberRepository;
    private final RewardBudgetRepository rewardBudgetRepository;

    public Account createAccount(Long userId) {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new AccountException(AccountErrorCode.MEMBER_NOT_FOUND));
            
        Account account = Account.builder()
            .member(member)
            .balance(0.0)
            .totalEarned(0.0)
            .pendingAmount(0.0)
            .status(AccountStatus.ACTIVE)
            .build();
            
        return accountRepository.save(account);
    }

    public void addBalance(Long userId, double amount, TransactionType type, String description) {
        Account account = accountRepository.findByMemberId(userId)
            .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        Transaction transaction = Transaction.builder()
            .account(account)
            .amount(amount)
            .type(type)
            .description(description)
            .build();

        account.addTransaction(transaction);
        transactionRepository.save(transaction);
    }

    public void registerBankAccount(Long userId, String bankName, String accountNumber, String accountHolder) {
        Account account = accountRepository.findByMemberId(userId)
            .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));
            
        account.updateBankInfo(bankName, accountNumber, accountHolder);
        accountRepository.save(account);
    }

    public void verifyBankAccount(Long userId) {
        Account account = accountRepository.findByMemberId(userId)
            .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));
            
        // 실제로는 외부 은행 API를 통해 계좌 실명 검증
        if (!account.hasBankInfo()) {
            throw new AccountException(AccountErrorCode.BANK_INFO_NOT_FOUND);
        }
    }

    public void depositByBankTransfer(Long userId, double amount, String transactionId) {
        Account account = accountRepository.findByMemberId(userId)
            .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));
            
        // 실제로는 입금내역 조회 API를 통해 검증
        if (isValidDeposit(transactionId)) {
            addBalance(userId, amount, TransactionType.BANK_TRANSFER, "계좌이체 입금");
        }
    }

    private boolean isValidDeposit(String transactionId) {
        // TODO: 실제로는 외부 은행 API를 통해 입금내역 검증
        // 테스트를 위해 항상 true 반환
        return true;
    }

    @Transactional(readOnly = true)
    public void validateBalance(Long memberId, double requiredAmount) {
        Account account = accountRepository.findByMemberId(memberId)
                .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        // 현재 진행 중인 미션들의 총 예산 계산
        double reservedBudget = rewardBudgetRepository.findAllByMemberIdAndStatusIn(
            memberId, 
            LocalDate.now()
        ).stream()
        .mapToDouble(RewardBudget::getTotalBudget)
        .sum();

        // 실제 사용 가능한 잔액 = 계정 잔액 - 이미 예약된 예산
        double availableBalance = account.getBalance() - reservedBudget;

        if (availableBalance < requiredAmount) {
            throw new AccountException(
                AccountErrorCode.INSUFFICIENT_BALANCE, 
                new RuntimeException(String.format(
                    "계정 잔액이 부족합니다. 필요 금액: %.0f원, 사용 가능한 잔액: %.0f원 (총 잔액: %.0f원, 예약된 금액: %.0f원)", 
                    requiredAmount, availableBalance, account.getBalance(), reservedBudget))
            );
        }
    }

    public Account getAccount(Long userId) {
        return accountRepository.findByMemberId(userId)
            .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));
    }
}