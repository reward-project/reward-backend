package com.outsider.reward.domain.tag.command.domain;

import com.outsider.reward.domain.member.command.domain.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.AccessLevel;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column
    private Long useCount = 0L;

    @Column
    private boolean isPublic = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = true)
    private Member createdBy;

    public Tag(String name, boolean isPublic, Member createdBy) {
        if (createdBy == null) {
            throw new IllegalArgumentException("Tag creator cannot be null");
        }
        this.name = name;
        this.isPublic = isPublic;
        this.createdBy = createdBy;
        this.useCount = 1L;
    }

    public static Tag createPublic(String name, Member createdBy) {
        return new Tag(name, true, createdBy);
    }

    public static Tag createPrivate(String name, Member createdBy) {
        return new Tag(name, false, createdBy);
    }

    public void incrementUseCount() {
        this.useCount++;
    }
} 