package com.outsider.reward.domain.finance.command.application;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.outsider.reward.domain.finance.command.dto.KakaoPayChargeRequest;
import com.outsider.reward.domain.finance.command.dto.KakaoPayReadyResponse;
import com.outsider.reward.domain.finance.command.dto.KakaoPayApproveResponse;
import com.outsider.reward.domain.finance.command.domain.TransactionType;
import com.outsider.reward.domain.finance.command.application.PaymentService;
import com.outsider.reward.domain.finance.command.dto.PaymentReadyResponse;
import com.outsider.reward.domain.finance.command.dto.PaymentRequest;

@Service
@RequiredArgsConstructor
@Profile("prod")  // 운영 환경에서 사용
public class KakaoPayService implements PaymentService {
    private final RestTemplate restTemplate;
    private final AccountService accountService;
    
    @Value("${kakaopay.cid}")
    private String cid;
    
    @Value("${kakaopay.secret}")
    private String secret;

    @Override
    public PaymentReadyResponse requestPayment(PaymentRequest request) {
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

        KakaoPayReadyResponse kakaoResponse = restTemplate.postForObject(
            "https://kapi.kakao.com/v1/payment/ready",
            body,
            KakaoPayReadyResponse.class
        );

        return PaymentReadyResponse.builder()
            .paymentKey(kakaoResponse.getTid())
            .orderId(request.getOrderId())
            .successUrl(kakaoResponse.getNextRedirectPcUrl())
            .build();
    }

    @Override
    @Transactional
    public void processPayment(String paymentKey, String orderId) {
        // 1. 카카오페이 결제 승인 요청
        KakaoPayApproveResponse response = approvePayment(paymentKey, orderId);
        
        // 2. 계정에 금액 추가
        accountService.addBalance(
            Long.parseLong(response.getPartnerUserId()), 
            response.getAmount().getTotal(),
            TransactionType.KAKAO_PAY,
            "카카오페이 충전: " + response.getOrderId()
        );
    }

    @Override
    public void cancelPayment(String paymentKey, String reason) {
        // 카카오페이 결제 취소 구현
    }

    private KakaoPayApproveResponse approvePayment(String paymentKey, String orderId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + secret);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", cid);
        params.add("tid", paymentKey);
        params.add("partner_order_id", orderId);

        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<>(params, headers);

        return restTemplate.postForObject(
            "https://kapi.kakao.com/v1/payment/approve",
            body,
            KakaoPayApproveResponse.class
        );
    }

    public void processPaymentSuccess(String pgToken, String orderId) {
        // 카카오페이 결제 승인 처리
        // 1. 카카오페이 승인 요청
        // 2. 결제 정보 업데이트
        // 3. 계좌 잔액 업데이트
    }
} 