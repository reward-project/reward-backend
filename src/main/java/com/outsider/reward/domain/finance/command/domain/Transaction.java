package com.outsider.reward.domain.finance.command.domain;

import com.outsider.reward.domain.store.command.domain.RewardUsage;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.outsider.reward.domain.tag.command.domain.BaseTimeEntity;

@Entity
@Table(name = "transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(nullable = false)
    private double amount;

    public double getAmount() {
        return amount;
    }

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_usage_id")
    private RewardUsage rewardUsage;

    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Builder
    public Transaction(Account account, double amount, TransactionType type, String description, RewardUsage rewardUsage) {
        this.account = account;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.rewardUsage = rewardUsage;
        this.status = TransactionStatus.PENDING;
        this.transactionDate = LocalDateTime.now();
    }

    public void complete() {
        this.status = TransactionStatus.COMPLETED;
    }

    public void fail(String reason) {
        this.status = TransactionStatus.FAILED;
        this.description += " (실패 사유: " + reason + ")";
    }
}