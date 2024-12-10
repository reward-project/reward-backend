package com.outsider.reward.domain.store.presentation;


import com.outsider.reward.domain.store.command.dto.MissionCompleteRequest;
import com.outsider.reward.domain.store.command.dto.MissionCompleteResponse;
import com.outsider.reward.domain.store.query.application.ActiveMissionQueryService;
import com.outsider.reward.domain.store.query.dto.StoreMissionQueryResponse;
import com.outsider.reward.global.common.response.ApiResponse;
import com.outsider.reward.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ActiveMissionController {

    private final ActiveMissionQueryService activeMissionQueryService;

    @GetMapping("/active-missions")
    public ResponseEntity<ApiResponse<Page<StoreMissionQueryResponse>>> getActiveMissions(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(
            activeMissionQueryService.getStoreMissions(userDetails.getId(), LocalDate.now(), pageable)));
    }

    @GetMapping("/active-missions/{id}")
    public ResponseEntity<ApiResponse<StoreMissionQueryResponse>> getActiveMission(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        StoreMissionQueryResponse mission = activeMissionQueryService.getStoreMission(userDetails.getId(), id);
        return ResponseEntity.ok(ApiResponse.success(mission, "활성 미션을 성공적으로 조회했습니다."));
    }

}
