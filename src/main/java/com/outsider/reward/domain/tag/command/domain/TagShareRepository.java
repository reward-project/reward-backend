package com.outsider.reward.domain.tag.command.domain;

import com.outsider.reward.domain.member.command.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagShareRepository extends JpaRepository<TagShare, Long> {
    boolean existsByTagAndSharedWith(Tag tag, Member sharedWith);
    List<TagShare> findBySharedWith(Member member);
    List<TagShare> findByTag(Tag tag);
    Optional<TagShare> findByTagAndSharedWith(Tag tag, Member sharedWith);
    void deleteByTagAndSharedWith(Tag tag, Member sharedWith);
} 