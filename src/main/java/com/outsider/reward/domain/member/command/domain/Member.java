package com.outsider.reward.domain.member.command.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor
public class Member {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Embedded
    private MemberBasicInfo basicInfo;
    
    @Embedded
    private OAuth oAuth;
    
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Builder
    public Member(String name, String email, String password, String nickname) {
        this.basicInfo = new MemberBasicInfo(name, email, password, nickname);
        this.emailVerified = false;
        this.createdAt = LocalDateTime.now();
    }
    
    public void verifyEmail() {
        this.emailVerified = true;
        this.updatedAt = LocalDateTime.now();
    }
} 