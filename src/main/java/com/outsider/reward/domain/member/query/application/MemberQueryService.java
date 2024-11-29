package com.outsider.reward.domain.member.query.application;

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
    
    public MemberQuery.MemberInfo getMemberInfo(Long memberId) {
        return memberQueryDao.findMemberInfo(memberId);
    }
} 