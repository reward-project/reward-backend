package com.outsider.reward.domain.finance.command.domain;

import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.tag.command.domain.BaseTimeEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(nullable = false)
    private String bankName;         // 은행명

    @Column(nullable = false)
    private String accountNumber;    // 계좌번호

    @Column(nullable = false)
    private String accountHolder;    // 예금주

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
} 