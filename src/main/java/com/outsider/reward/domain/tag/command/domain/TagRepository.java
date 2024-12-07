package com.outsider.reward.domain.tag.command.domain;

import com.outsider.reward.domain.member.command.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
    
    // 공개 태그나 본인이 만든 태그 검색
    @Query("SELECT t FROM Tag t WHERE (t.isPublic = true OR t.createdBy = :member) AND LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Tag> searchTags(@Param("query") String query, @Param("member") Member member);
    
    // 공개 태그만 검색
    @Query("SELECT t FROM Tag t WHERE t.isPublic = true AND LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Tag> searchPublicTags(@Param("query") String query);
    
    // 인 태그만 검색
    @Query("SELECT t FROM Tag t WHERE t.isPublic = false AND t.createdBy = :member AND LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Tag> searchPrivateTags(@Param("query") String query, @Param("member") Member member);
    
    // 본인이 만든 태그 목록
    List<Tag> findByCreatedBy(Member member);
    
    // 공개 태그 목록
    List<Tag> findByIsPublicTrue();
    
    // 인기 태그 (공개 태그만)
    List<Tag> findTop10ByIsPublicTrueOrderByUseCountDesc();
    
    @Query("SELECT t FROM Tag t LEFT JOIN TagShare ts ON t = ts.tag " +
           "WHERE t.createdBy.id = :userId OR ts.sharedWith.id = :userId")
    List<Tag> findByUserIdIncludingShared(@Param("userId") Long userId);
} 