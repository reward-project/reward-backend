package com.outsider.reward.domain.member.command.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "oauth_providers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String provider;    // google, kakao 등

    @Column(nullable = false)
    private String providerId;  // 제공자의 식별자

    private LocalDateTime connectedAt;

    @Builder
    public OAuthProvider(Member member, String provider, String providerId) {
        this.member = member;
        this.provider = provider;
        this.providerId = providerId;
        this.connectedAt = LocalDateTime.now();
    }
} 