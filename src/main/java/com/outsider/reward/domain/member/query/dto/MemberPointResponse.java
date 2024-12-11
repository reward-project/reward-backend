package com.outsider.reward.domain.member.query.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberPointResponse {
    private final double point;

    public static MemberPointResponse of(double point) {
        return MemberPointResponse.builder()
            .point(point)
            .build();
    }
}
