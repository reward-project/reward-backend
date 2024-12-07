package com.outsider.reward.domain.tag.command.dto;

import com.outsider.reward.domain.tag.command.domain.TagSharePermission;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShareTagRequest {
    private Long sharedWithId;
    private TagSharePermission permission;
} 