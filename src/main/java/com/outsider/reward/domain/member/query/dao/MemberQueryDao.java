package com.outsider.reward.domain.member.query.dao;

import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.query.dto.MemberQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberQueryDao extends JpaRepository<Member, Long> {
    @Query("SELECT new com.outsider.reward.domain.member.query.dto.MemberQuery$MemberInfo(" +
           "m.id, m.basicInfo.name, m.basicInfo.email, m.basicInfo.nickname, m.basicInfo.profileImageUrl) " +
           "FROM Member m WHERE m.id = :id")
    MemberQuery.MemberInfo findMemberInfo(@Param("id") Long id);
} 