package com.outsider.reward.domain.member.query.dto;
    
import java.time.LocalDateTime;

import com.outsider.reward.domain.member.command.domain.Member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {
    private final Long id;
    private final String email;
    private final String name;
    private final String nickname;
    private final String profileImage;
    private final LocalDateTime createdAt;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
            .id(member.getId())
            .email(member.getEmail())
            .name(member.getName())
            .nickname(member.getNickname())
            .profileImage(member.getProfileImageUrl())
            .createdAt(member.getCreatedAt())
            .build();
    }
} 