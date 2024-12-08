package com.outsider.reward.domain.finance.command.application;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MultiValueMap;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import com.outsider.reward.domain.finance.command.dto.KakaoPayChargeRequest;
import com.outsider.reward.domain.finance.command.dto.KakaoPayReadyResponse;
import com.outsider.reward.domain.finance.command.dto.KakaoPayApproveResponse;
import com.outsider.reward.domain.finance.command.service.AccountService;
import com.outsider.reward.domain.finance.command.service.TransactionType;

@Service
@RequiredArgsConstructor
public class KakaoPayService {
    private final RestTemplate restTemplate;
    private final AccountService accountService;
    
    @Value("${kakaopay.cid}")
    private String cid;
    
    @Value("${kakaopay.secret}")
    private String secret;

    public KakaoPayReadyResponse requestPayment(KakaoPayChargeRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + secret);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", cid);
        params.add("partner_order_id", request.getOrderId());
        params.add("partner_user_id", request.getUserId().toString());
        params.add("item_name", request.getItemName());
        params.add("quantity", "1");
        params.add("total_amount", String.valueOf((int)request.getAmount()));
        params.add("tax_free_amount", "0");
        params.add("approval_url", "http://localhost:8080/api/v1/payments/kakao/success");
        params.add("cancel_url", "http://localhost:8080/api/v1/payments/kakao/cancel");
        params.add("fail_url", "http://localhost:8080/api/v1/payments/kakao/fail");

        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<>(params, headers);

        return restTemplate.postForObject(
            "https://kapi.kakao.com/v1/payment/ready",
            body,
            KakaoPayReadyResponse.class
        );
    }

    @Transactional
    public void processPaymentSuccess(String pgToken, String orderId) {
        // 1. 카카오페이 결제 승인 요청
        KakaoPayApproveResponse response = approvePayment(pgToken, orderId);
        
        // 2. 계정에 금액 추가
        accountService.addBalance(
            Long.parseLong(response.getPartnerUserId()), 
            response.getAmount().getTotal(),
            TransactionType.KAKAO_PAY,
            "카카오페이 충전: " + response.getOrderId()
        );
    }
} 