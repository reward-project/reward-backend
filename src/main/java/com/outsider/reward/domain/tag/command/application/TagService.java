package com.outsider.reward.domain.tag.command.application;

import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.domain.MemberRepository;
import com.outsider.reward.domain.member.exception.MemberErrorCode;
import com.outsider.reward.domain.member.exception.MemberException;
import com.outsider.reward.domain.tag.command.domain.Tag;
import com.outsider.reward.domain.tag.command.domain.TagRepository;
import com.outsider.reward.domain.tag.command.domain.TagShare;
import com.outsider.reward.domain.tag.command.domain.TagShareRepository;
import com.outsider.reward.domain.tag.command.domain.TagSharePermission;
import com.outsider.reward.domain.tag.exception.TagErrorCode;
import com.outsider.reward.domain.tag.exception.TagException;
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
    private final TagShareRepository tagShareRepository;

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

    @Transactional
    public void shareTag(Long tagId, Long sharedWithId, TagSharePermission permission, Long ownerId) {
        Tag tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new TagException(TagErrorCode.TAG_NOT_FOUND));
            
        // 태그 소유 확인
        if (!tag.getCreatedBy().getId().equals(ownerId)) {
            throw new TagException(TagErrorCode.NOT_TAG_OWNER);
        }

        Member sharedWith = memberRepository.findById(sharedWithId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 이미 공유된 태그인지 확인
        if (tagShareRepository.existsByTagAndSharedWith(tag, sharedWith)) {
            throw new TagException(TagErrorCode.ALREADY_SHARED);
        }

        TagShare tagShare = new TagShare(tag, tag.getCreatedBy(), sharedWith, permission);
        tagShareRepository.save(tagShare);
    }
} 