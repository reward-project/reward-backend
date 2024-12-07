package com.outsider.reward.domain.tag.command.application;

import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.domain.member.exception.MemberErrorCode;
import com.outsider.reward.domain.member.exception.MemberException;
import com.outsider.reward.domain.tag.command.domain.Tag;
import com.outsider.reward.domain.tag.command.domain.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final MemberRepository memberRepository;

    public List<String> searchTags(String query, Long userId) {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
            
        return tagRepository.searchTags(query, member).stream()
            .map(Tag::getName)
            .collect(Collectors.toList());
    }

    public List<String> searchPublicTags(String query) {
        return tagRepository.searchPublicTags(query).stream()
            .map(Tag::getName)
            .collect(Collectors.toList());
    }

    public List<String> getMyTags(Long userId) {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
            
        return tagRepository.findByCreatedBy(member).stream()
            .map(Tag::getName)
            .collect(Collectors.toList());
    }

    public List<String> getPopularTags() {
        return tagRepository.findTop10ByIsPublicTrueOrderByUseCountDesc().stream()
            .map(Tag::getName)
            .collect(Collectors.toList());
    }

    public List<String> searchPrivateTags(String query, Long userId) {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
            
        return tagRepository.searchPrivateTags(query, member).stream()
            .map(Tag::getName)
            .collect(Collectors.toList());
    }

    @Transactional
    public Tag createTag(String name, Member creator, boolean isPublic) {
        return tagRepository.save(new Tag(name, isPublic, creator));
    }
} 