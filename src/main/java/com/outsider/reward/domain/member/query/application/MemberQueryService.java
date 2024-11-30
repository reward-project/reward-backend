package com.outsider.reward.domain.member.query.application;

import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.domain.member.exception.MemberException;
import com.outsider.reward.domain.member.exception.MemberErrorCode;
import com.outsider.reward.domain.member.query.dao.MemberQueryDao;
import com.outsider.reward.domain.member.query.dto.MemberQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberQueryDao memberQueryDao;
    private final MemberRepository memberRepository;
    
    public MemberQuery.MemberInfo getMemberInfo(Long memberId) {
        return memberQueryDao.findMemberInfo(memberId);
    }

    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
} 