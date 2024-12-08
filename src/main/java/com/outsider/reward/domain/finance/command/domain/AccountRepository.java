package com.outsider.reward.domain.finance.command.domain;

import com.outsider.reward.domain.member.command.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByMember(Member member);
    Optional<Account> findByMemberId(Long memberId);
} 