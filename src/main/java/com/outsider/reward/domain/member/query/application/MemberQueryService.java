package com.outsider.reward.domain.member.query.application;

import com.outsider.reward.domain.finance.command.domain.Account;
import com.outsider.reward.domain.finance.command.domain.AccountRepository;
import com.outsider.reward.domain.finance.command.domain.Transaction;
import com.outsider.reward.domain.finance.command.domain.TransactionRepository;
import com.outsider.reward.domain.finance.command.domain.TransactionType;
import com.outsider.reward.domain.member.exception.MemberErrorCode;
import com.outsider.reward.domain.member.exception.MemberException;
import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.domain.member.query.dao.MemberQueryDao;
import com.outsider.reward.domain.member.query.dto.MemberPointResponse;
import com.outsider.reward.domain.member.query.dto.MemberQuery;
import com.outsider.reward.domain.member.query.dto.CashHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberQueryDao memberQueryDao;
    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    
    public MemberQuery.MemberInfo getMemberInfo(Long memberId) {
        return memberQueryDao.findMemberInfo(memberId);
    }

    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public MemberPointResponse getPoint(Long memberId) {
        Account account = accountRepository.findByMemberId(memberId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        return MemberPointResponse.of(account.getTotalEarned());
    }

    @Transactional(readOnly = true)
    public Page<CashHistoryResponse> getCashHistory(Long memberId, String type, Pageable pageable) {
        Account account = accountRepository.findByMemberId(memberId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
            
        Page<Transaction> transactions;
        
        switch (type) {
            case "EARN":
                transactions = transactionRepository.findByAccountAndTypeOrderByTransactionDateDesc(
                    account, TransactionType.EARN, pageable);
                break;
            case "PAYMENT":
                transactions = transactionRepository.findByAccountAndTypeInOrderByTransactionDateDesc(
                    account, 
                    List.of(TransactionType.WITHDRAWAL, TransactionType.KAKAO_PAY, 
                           TransactionType.NAVER_PAY, TransactionType.BANK_TRANSFER),
                    pageable);
                break;
            default:
                transactions = transactionRepository.findByAccountOrderByTransactionDateDesc(account, pageable);
        }
        
        return transactions.map(transaction -> CashHistoryResponse.builder()
            .id(transaction.getId())
            .amount(transaction.getAmount())
            .type(transaction.getType())
            .description(transaction.getDescription())
            .createdAt(transaction.getTransactionDate())
            .balance(account.getBalance()) // TODO: 각 거래 시점의 잔액 계산 필요
            .build());
    }
} 