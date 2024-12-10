package com.outsider.reward.domain.store.presentation;

import com.outsider.reward.domain.store.command.application.MissionCompletionService;
import com.outsider.reward.domain.store.command.dto.MissionCompleteRequest;
import com.outsider.reward.domain.store.command.dto.MissionCompleteResponse;
import com.outsider.reward.global.common.response.ApiResponse;
import com.outsider.reward.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MissionCompletionController {

    private final MissionCompletionService missionCompletionService;

    @PostMapping("/missions/{id}/complete")
    public ResponseEntity<ApiResponse<MissionCompleteResponse>> completeMission(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestBody MissionCompleteRequest request) {
        request.setUserId(userDetails.getId());
        MissionCompleteResponse response = missionCompletionService.completeMissionWithResponse(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "미션을 성공적으로 완료했습니다."));
    }
}
