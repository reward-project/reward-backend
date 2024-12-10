package com.outsider.reward.domain.finance.command.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class NaverPayment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String orderId;
    private String paymentId;
    private Double amount;
    private String productName;
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    
    @Builder
    public NaverPayment(String orderId, String paymentId, Double amount, String productName, Long userId) {
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.amount = amount;
        this.productName = productName;
        this.userId = userId;
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    public void complete() {
        this.status = PaymentStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
    
    public void fail() {
        this.status = PaymentStatus.FAILED;
    }
} 