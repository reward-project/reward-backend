package com.outsider.reward.domain.finance.command.application;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("dev")  // 로컬 환경에서만 사용
public class MockBankTransferService implements BankTransferService {

    @Override
    public void requestTransfer(String bankCode, String accountNumber, double amount, String message) {
        log.info("Mock Bank Transfer Request - Bank: {}, Account: {}, Amount: {}, Message: {}", 
            bankCode, accountNumber, amount, message);
        
        // 실제 이체 대신 로그만 남김
        try {
            Thread.sleep(1000); // 1초 지연
            log.info("Mock Bank Transfer Success");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Mock transfer interrupted", e);
        }
    }

    @Override
    public void verifyAccount(String bankCode, String accountNumber, String accountHolder) {
        log.info("Mock Account Verification - Bank: {}, Account: {}, Holder: {}", 
            bankCode, accountNumber, accountHolder);
    }

    @Override
    public boolean validateDeposit(String bankCode, String accountNumber, String transactionId) {
        log.info("Mock Deposit Validation - Bank: {}, Account: {}, Transaction: {}", 
            bankCode, accountNumber, transactionId);
        return true;
    }
} 