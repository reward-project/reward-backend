package com.outsider.reward.domain.finance.command.application;

public interface BankTransferService {
    void verifyAccount(String bankCode, String accountNumber, String accountHolder);
    void requestTransfer(String bankCode, String accountNumber, double amount, String purpose);
    boolean validateDeposit(String bankCode, String accountNumber, String transactionId);
} 