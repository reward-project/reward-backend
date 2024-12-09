package com.outsider.reward.domain.finance.command.repository;

import com.outsider.reward.domain.finance.command.domain.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    // 필요한 경우 추가 쿼리 메서드 정의
} 