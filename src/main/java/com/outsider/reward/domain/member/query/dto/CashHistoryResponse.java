package com.outsider.reward.domain.member.query.dto;

import java.time.LocalDateTime;

import com.outsider.reward.domain.finance.command.domain.TransactionType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CashHistoryResponse {
    private final Long id;
    private final double amount;
    private final TransactionType type;
    private final String description;
    private final LocalDateTime createdAt;
    private final double balance;
}
