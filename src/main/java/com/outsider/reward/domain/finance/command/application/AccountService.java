package com.outsider.reward.domain.finance.command.application;

import com.outsider.reward.domain.finance.command.domain.Account;
import com.outsider.reward.domain.finance.command.domain.AccountRepository;
import com.outsider.reward.domain.finance.command.domain.AccountStatus;
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

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final MemberRepository memberRepository;

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

    public Account getAccount(Long userId) {
        return accountRepository.findByMemberId(userId)
            .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));
    }
} 