package com.outsider.reward.domain.member.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByBasicInfo_Email(String email);
    boolean existsByBasicInfo_Email(String email);
    boolean existsByBasicInfo_Nickname(String nickname);

    default Optional<Member> findByEmail(String email) {
        return findByBasicInfo_Email(email);
    }
} 