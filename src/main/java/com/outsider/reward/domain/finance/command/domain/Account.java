package com.outsider.reward.domain.finance.command.domain;

import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.tag.command.domain.BaseTimeEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Account extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private double balance;          // 현재 잔액

    @Column(nullable = false)
    private double totalEarned;      // 총 획득 금액

    @Column(nullable = false)
    private double pendingAmount;    // 정산 대기 금액

    @Column(nullable = true)
    private String bankName;

    @Column(nullable = true)
    private String accountNumber;

    @Column(nullable = true)
    private String accountHolder;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;    // 계좌 상태

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Settlement> settlements = new ArrayList<>();

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        this.balance += transaction.getAmount();
        if (transaction.getType() == TransactionType.REWARD) {
            this.totalEarned += transaction.getAmount();
            this.pendingAmount += transaction.getAmount();
        }
    }

    public void addSettlement(Settlement settlement) {
        this.settlements.add(settlement);
        this.pendingAmount -= settlement.getAmount();
    }

    public void addBalance(double amount, TransactionType type, String description) {
        this.balance += amount;
        if (type == TransactionType.KAKAO_PAY) {
            this.totalEarned += amount;  // 카카오페이 충전도 총 적립금에 포함
        }
    }

    public void updateBankInfo(String bankName, String accountNumber, String accountHolder) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
    }

    public boolean hasBankInfo() {
        return bankName != null && accountNumber != null && accountHolder != null;
    }
} 