package com.outsider.reward.domain.member.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByBasicInfo_Email(String email);
    boolean existsByBasicInfo_Nickname(String nickname);
    Optional<Member> findByBasicInfo_Email(String email);
} 