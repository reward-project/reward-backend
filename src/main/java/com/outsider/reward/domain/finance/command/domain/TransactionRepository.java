package com.outsider.reward.domain.finance.command.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount_Member_Id(Long memberId);
    
    // 전체 내역 조회 (페이지네이션)
    Page<Transaction> findByAccountOrderByTransactionDateDesc(Account account, Pageable pageable);
    
    // 적립 내역만 조회 (EARN)
    Page<Transaction> findByAccountAndTypeOrderByTransactionDateDesc(
        Account account, TransactionType type, Pageable pageable);
    
    // 출금/충전 내역 조회 (WITHDRAWAL, KAKAO_PAY, NAVER_PAY, BANK_TRANSFER)
    Page<Transaction> findByAccountAndTypeInOrderByTransactionDateDesc(
        Account account, List<TransactionType> types, Pageable pageable);
}