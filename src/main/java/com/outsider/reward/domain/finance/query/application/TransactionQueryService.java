package com.outsider.reward.domain.finance.query.application;

import com.outsider.reward.domain.finance.command.domain.Transaction;
import com.outsider.reward.domain.finance.command.domain.TransactionRepository;
import com.outsider.reward.domain.finance.query.dto.TransactionDto;
import com.outsider.reward.domain.finance.query.dto.TransactionListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionQueryService {

    private final TransactionRepository transactionRepository;

    public TransactionListResponse getTransactions(Long memberId) {
        List<Transaction> transactions = transactionRepository.findByAccount_Member_Id(memberId);
        
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(TransactionDto::from)
                .collect(Collectors.toList());
                
        return new TransactionListResponse(transactionDtos);
    }
}
