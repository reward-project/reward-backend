package com.outsider.reward.domain.store.command.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DeleteStoreMissionsRequest {
    @NotEmpty(message = "삭제할 리워드 ID 목록은 비어있을 수 없습니다.")
    private List<Long> missionIds;
}
