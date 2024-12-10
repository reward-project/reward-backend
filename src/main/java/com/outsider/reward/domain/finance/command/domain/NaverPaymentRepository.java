package com.outsider.reward.domain.finance.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NaverPaymentRepository extends JpaRepository<NaverPayment, Long> {
    Optional<NaverPayment> findByOrderId(String orderId);
    Optional<NaverPayment> findByPaymentId(String paymentId);
} 