package com.outsider.reward.domain.finance.command.application;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"local", "dev"})
public class MockBankTransferService implements BankTransferService {
    @Override
    public void verifyAccount(String bankCode, String accountNumber, String accountHolder) {
        // 테스트용으로 항상 성공
    }

    @Override
    public void requestTransfer(String bankCode, String accountNumber, double amount, String purpose) {
        // 테스트용으로 항상 성공
    }

    @Override
    public boolean validateDeposit(String bankCode, String accountNumber, String transactionId) {
        return true;
    }
} 