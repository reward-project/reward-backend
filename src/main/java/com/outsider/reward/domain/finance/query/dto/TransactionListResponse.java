package com.outsider.reward.domain.finance.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TransactionListResponse {
    private final List<TransactionDto> transactions;
}
