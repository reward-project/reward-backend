package com.outsider.reward.domain.finance.command.domain;

import com.outsider.reward.domain.tag.command.domain.BaseTimeEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "settlements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Settlement extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private LocalDateTime settlementDate;

    @Enumerated(EnumType.STRING)
    private SettlementStatus status;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String accountHolder;

    @Builder
    public Settlement(Account account, double amount, String bankName, 
                     String accountNumber, String accountHolder) {
        this.account = account;
        this.amount = amount;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.settlementDate = LocalDateTime.now();
        this.status = SettlementStatus.PENDING;
    }

    public void complete() {
        this.status = SettlementStatus.COMPLETED;
    }

    public void fail(String reason) {
        this.status = SettlementStatus.FAILED;
    }
} 