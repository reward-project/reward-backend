package com.outsider.reward.domain.finance.command.application;

import com.outsider.reward.domain.finance.command.domain.NaverPayment;
import com.outsider.reward.domain.finance.command.domain.TransactionType;
import com.outsider.reward.domain.finance.command.dto.PaymentReadyResponse;
import com.outsider.reward.domain.finance.command.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.outsider.reward.domain.finance.command.domain.NaverPaymentRepository;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Primary
@Profile("dev")
@RequiredArgsConstructor
public class NaverPayService implements PaymentService {

    private static final String NAVER_PAY_API_URL = "https://dev.apis.naver.com";
    private static final String MERCHANT_ID = "np_tawxm504875";
    
    private final RestTemplate restTemplate;
    private final AccountService accountService;
    private final NaverPaymentRepository naverPaymentRepository;
    
    @Value("${naverpay.client.id}")
    private String clientId;
    
    @Value("${naverpay.client.secret}")
    private String clientSecret;
    
    @Value("${naverpay.chain.id}")
    private String chainId;

    @Value("${app.backend.url}")
    private String backendUrl;

    @Override
    @Transactional
    public PaymentReadyResponse requestPayment(PaymentRequest request) {
        String paymentId = UUID.randomUUID().toString();
        
        NaverPayment payment = NaverPayment.builder()
                .orderId(request.getOrderId())
                .paymentId(paymentId)
                .amount(request.getAmount())
                .productName(request.getItemName())
                .userId(request.getUserId())
                .build();
                
        naverPaymentRepository.save(payment);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        headers.set("X-NaverPay-Chain-Id", chainId);
        headers.set("X-NaverPay-Idempotency-Key", UUID.randomUUID().toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("merchantId", MERCHANT_ID);
        body.put("merchantPayKey", paymentId);
        body.put("productName", request.getItemName());
        body.put("totalPayAmount", (int)request.getAmount());
        body.put("taxScopeAmount", (int)request.getAmount());
        body.put("taxExScopeAmount", 0);
        body.put("returnUrl", backendUrl + "/api/v1/payments/naver/callback");

        body.put("paymentMethod", "CARD");
        body.put("currency", "KRW");
        body.put("deliveryFee", 0);
        body.put("isExchangeable", false);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            log.debug("네이버페이 결제 요청: {}", body);
            ResponseEntity<Map> response = restTemplate.exchange(
                NAVER_PAY_API_URL + "/" + MERCHANT_ID + "/naverpay/payments/v2.2/reserve",
                HttpMethod.POST,
                requestEntity,
                Map.class
            );

            log.debug("네이버페이 응답: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                
                log.debug("네이버페이 응답 body: {}", responseBody);
                
                String paymentUrl = (String) responseBody.get("paymentUrl");
                if (paymentUrl == null) {
                    paymentUrl = (String) responseBody.get("redirectUrl");
                }
                if (paymentUrl == null) {
                    paymentUrl = (String) responseBody.get("url");
                }
                if (paymentUrl == null && responseBody.get("result") instanceof Map) {
                    Map<String, Object> result = (Map<String, Object>) responseBody.get("result");
                    paymentUrl = (String) result.get("paymentUrl");
                }
                
                if (paymentUrl == null) {
                    log.error("네이버페이 결제 URL을 찾을 수 없습니다. 응답: {}", responseBody);
                    throw new RuntimeException("네이버페이 결제 URL이 없습니다");
                }

                return PaymentReadyResponse.builder()
                    .paymentKey(paymentId)
                    .orderId(paymentId)
                    .successUrl(paymentUrl)
                    .failUrl("/api/v1/payments/naver/fail")
                    .cancelUrl("/api/v1/payments/naver/cancel")
                    .build();
            } else {
                log.error("네이버페이 결제 준비 실패. 응답: {}", response.getBody());
                throw new RuntimeException("네이버페이 결제 준비 요청 실패");
            }
        } catch (Exception e) {
            log.error("네이버페이 결제 준비 요청 중 오류 발생", e);
            throw new RuntimeException("네이버페이 결제 준비 요청 실패: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void processPayment(String paymentKey, String orderId) {
        NaverPayment payment = naverPaymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        headers.set("X-NaverPay-Chain-Id", chainId);
        headers.set("X-NaverPay-Idempotency-Key", UUID.randomUUID().toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("paymentId", paymentKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                NAVER_PAY_API_URL + "/" + MERCHANT_ID + "/naverpay/payments/v2.2/apply/payment",
                HttpMethod.POST,
                requestEntity,
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                payment.complete();
                
                // 계정에 금액 추가
                accountService.addBalance(
                    payment.getUserId(),
                    payment.getAmount(),
                    TransactionType.NAVER_PAY,
                    "네이버페이 충전: " + orderId
                );
            } else {
                throw new RuntimeException("네이버페이 결제 승인 요청 실패");
            }
        } catch (Exception e) {
            log.error("네이버페이 결제 승인 요청 중 오류 발생", e);
            throw new RuntimeException("네이버페이 결제 승인 요청 실패", e);
        }
    }

    @Override
    public void cancelPayment(String paymentKey, String reason) {
        NaverPayment payment = naverPaymentRepository.findByPaymentId(paymentKey)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        headers.set("X-NaverPay-Chain-Id", chainId);
        headers.set("X-NaverPay-Idempotency-Key", UUID.randomUUID().toString());
        headers.setContentType(MediaType.APPLICATION_JSON);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("paymentId", paymentKey);
        params.add("cancelAmount", payment.getAmount().toString());
        params.add("cancelReason", reason);
        params.add("cancelRequester", "2"); // 가맹점 리자

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                NAVER_PAY_API_URL + "/" + MERCHANT_ID + "/naverpay/payments/v2.2/cancel",
                HttpMethod.POST,
                requestEntity,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                payment.fail();
            } else {
                throw new RuntimeException("네이버페이 결제 취소 요청 실패");
            }
        } catch (Exception e) {
            log.error("네이버페이 결제 취소 요청 중 오류 발생", e);
            throw new RuntimeException("네이버페이 결제 취소 요청 실패", e);
        }
    }
} 