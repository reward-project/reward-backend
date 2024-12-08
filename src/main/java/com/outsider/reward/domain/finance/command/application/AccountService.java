package com.outsider.reward.domain.finance.command.application;

import com.outsider.reward.domain.finance.command.domain.Account;
import com.outsider.reward.domain.finance.command.domain.AccountRepository;
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
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addBalance(Long userId, double amount, TransactionType type, String description) {
        Account account = accountRepository.findByMemberId(userId)
            .orElseGet(() -> createNewAccount(userId));

        Transaction transaction = Transaction.builder()
            .account(account)
            .amount(amount)
            .type(type)
            .description(description)
            .build();

        account.addTransaction(transaction);
        transactionRepository.save(transaction);
    }

    @Transactional
    public void virtualCharge(Long userId, double amount) {
        addBalance(userId, amount, TransactionType.VIRTUAL_CHARGE, "가상 충전");
    }

    @Transactional
    public void adminCharge(Long userId, double amount, String reason) {
        addBalance(userId, amount, TransactionType.ADMIN_CHARGE, "관리자 충전: " + reason);
    }

    private Account createNewAccount(Long userId) {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new AccountException(AccountErrorCode.MEMBER_NOT_FOUND));
            
        Account account = Account.builder()
            .member(member)
            .balance(0.0)
            .build();
            
        return accountRepository.save(account);
    }

    public double getBalance(Long userId) {
        return accountRepository.findByMemberId(userId)
            .map(Account::getBalance)
            .orElse(0.0);
    }
} 