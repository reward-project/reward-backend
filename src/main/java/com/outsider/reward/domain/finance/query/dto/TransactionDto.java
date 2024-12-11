package com.outsider.reward.domain.finance.query.dto;

import com.outsider.reward.domain.finance.command.domain.Transaction;
import com.outsider.reward.domain.finance.command.domain.TransactionStatus;
import com.outsider.reward.domain.finance.command.domain.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionDto {
    private final Long id;
    private final double amount;
    private final TransactionType type;
    private final TransactionStatus status;
    private final String description;
    private final LocalDateTime timestamp;

    public static TransactionDto from(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .description(transaction.getDescription())
                .timestamp(transaction.getTransactionDate())
                .build();
    }
}
