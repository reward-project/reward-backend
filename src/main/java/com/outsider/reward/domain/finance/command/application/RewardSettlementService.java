package com.outsider.reward.domain.finance.command.application;

import com.outsider.reward.domain.finance.command.domain.Account;
import com.outsider.reward.domain.finance.command.dto.toss.TossTransferRequest;
import com.outsider.reward.domain.finance.exception.AccountErrorCode;
import com.outsider.reward.domain.finance.exception.AccountException;
import com.outsider.reward.domain.finance.command.domain.Settlement;
import com.outsider.reward.domain.finance.command.repository.SettlementRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RewardSettlementService {
    private final BankTransferService bankTransferService;
    private final AccountService accountService;
    private final SettlementRepository settlementRepository;
    
    public void settleReward(Long userId, double amount) {
        // 1. 계좌 정보 조회
        Account account = accountService.getAccount(userId);
        validateSettlement(account, amount);
        
        // 2. 지급 요청
        String orderId = generateOrderId();
        try {
            bankTransferService.requestTransfer(
                account.getBankCode(),
                account.getAccountNumber(), 
                amount,
                "리워드 정산_" + orderId
            );
            
            // 3. 정산 처리
            Settlement settlement = Settlement.builder()
                .account(account)
                .amount(amount)
                .bankName(account.getBankName())
                .accountNumber(account.getAccountNumber())
                .accountHolder(account.getAccountHolder())
                .orderId(orderId)
                .build();
            
            account.addSettlement(settlement);
            settlementRepository.save(settlement);
            
        } catch (Exception e) {
            throw new AccountException(AccountErrorCode.SETTLEMENT_FAILED);
        }
    }

    private void validateSettlement(Account account, double amount) {
        if (amount > account.getPendingAmount()) {
            throw new AccountException(AccountErrorCode.INVALID_SETTLEMENT_AMOUNT);
        }
    }

    private String generateOrderId() {
        return "REWARD_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
} 