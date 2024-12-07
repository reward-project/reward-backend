package com.outsider.reward.domain.tag.command.domain;


import com.outsider.reward.domain.member.command.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TagShare extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Member owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_with_id")
    private Member sharedWith;

    @Enumerated(EnumType.STRING)
    private TagSharePermission permission;

    public TagShare(Tag tag, Member owner, Member sharedWith, TagSharePermission permission) {
        this.tag = tag;
        this.owner = owner;
        this.sharedWith = sharedWith;
        this.permission = permission;
    }
}
