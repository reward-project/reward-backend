package com.outsider.reward.domain.finance.command.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.outsider.reward.domain.finance.exception.AccountErrorCode;
import com.outsider.reward.domain.finance.exception.AccountException;
import com.outsider.reward.domain.finance.command.dto.toss.TossAccountResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Profile("prod")
@Slf4j
public class TossBankTransferService implements BankTransferService {
    private static final String TOSS_API_URL = "https://api.tosspayments.com/v1";
    
    @Value("${toss.client.api-key}")
    private String apiKey;
    
    private final RestTemplate restTemplate;

    @Override
    public void verifyAccount(String bankCode, String accountNumber, String accountHolder) {
        HttpHeaders headers = createHeaders();
        
        Map<String, String> body = Map.of(
            "bank", bankCode,
            "accountNumber", accountNumber,
            "holderName", accountHolder
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        
        try {
            TossAccountResponse response = restTemplate.postForObject(
                TOSS_API_URL + "/bank/account/verify",
                request,
                TossAccountResponse.class
            );
            
            if (!response.isSuccess()) {
                throw new AccountException(AccountErrorCode.BANK_INFO_INVALID);
            }
        } catch (HttpClientErrorException e) {
            log.error("Toss account verification failed: {}", e.getMessage());
            throw new AccountException(AccountErrorCode.BANK_INFO_INVALID);
        }
    }

    @Override
    public void requestTransfer(String bankCode, String accountNumber, double amount, String purpose) {
        HttpHeaders headers = createHeaders();
        
        Map<String, Object> body = Map.of(
            "bank", bankCode,
            "accountNumber", accountNumber,
            "amount", amount,
            "purpose", purpose,
            "transferType", "NORMAL"
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        
        try {
            restTemplate.postForObject(
                TOSS_API_URL + "/transfer/withdraw",
                request,
                Void.class
            );
        } catch (HttpClientErrorException e) {
            log.error("Toss transfer failed: {}", e.getMessage());
            throw new AccountException(AccountErrorCode.TRANSFER_FAILED);
        }
    }

    @Override
    public boolean validateDeposit(String bankCode, String accountNumber, String transactionId) {
        try {
            HttpHeaders headers = createHeaders();
            
            Map<String, String> body = Map.of(
                "bank", bankCode,
                "accountNumber", accountNumber,
                "transactionId", transactionId
            );

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            
            TossAccountResponse response = restTemplate.postForObject(
                TOSS_API_URL + "/transfer/validate",
                request,
                TossAccountResponse.class
            );
            
            return response.isSuccess();
        } catch (HttpClientErrorException e) {
            log.error("Toss deposit validation failed: {}", e.getMessage());
            return false;
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        return headers;
    }
} 