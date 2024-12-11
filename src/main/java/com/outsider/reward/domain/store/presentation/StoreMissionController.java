package com.outsider.reward.domain.store.presentation;

import com.outsider.reward.domain.store.command.application.StoreMissionCommandService;
import com.outsider.reward.domain.store.command.domain.StoreMission;
import com.outsider.reward.domain.store.command.dto.CreateStoreMissionRequest;
import com.outsider.reward.domain.store.command.dto.StoreMissionResponse;
import com.outsider.reward.domain.store.command.dto.UpdateStoreMissionRequest;
import com.outsider.reward.domain.store.command.dto.DeleteStoreMissionsRequest;
import com.outsider.reward.domain.store.command.mapper.StoreMissionMapper;
import com.outsider.reward.domain.store.query.application.StoreMissionQueryService;
import com.outsider.reward.domain.store.query.dto.StoreMissionQueryDto;
import com.outsider.reward.global.security.CustomUserDetails;
import com.outsider.reward.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/store-missions")
@RequiredArgsConstructor
public class StoreMissionController {

    private final StoreMissionCommandService commandService;
    private final StoreMissionQueryService queryService;
    private final StoreMissionMapper storeMissionMapper;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<StoreMissionResponse>> createStoreMission(
            @Valid @RequestBody CreateStoreMissionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            log.debug("Creating store mission with request: {}", request);
            CreateStoreMissionRequest requestWithUserId = storeMissionMapper.toRequestWithUserId(request, userDetails.getMember().getId());
            StoreMissionResponse response = commandService.createStoreMission(requestWithUserId);
            return ResponseEntity.ok(ApiResponse.success(response, "리워드가 성공적으로 등록되었습니다."));
        } catch (Exception e) {
            log.error("Error creating store mission", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StoreMissionQueryDto>> getStoreMission(@PathVariable Long id) {
        return queryService.findById(id)
                .map(dto -> ResponseEntity.ok(ApiResponse.success(dto, "스토어 미션을 성공적으로 조회했습니다.")))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<StoreMissionQueryDto>>> getMyStoreMissions(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            log.debug("Fetching store missions for user: {}", userDetails.getMember().getId());
            List<StoreMissionQueryDto> missions = queryService.findByRegistrantId(userDetails.getMember().getId());
            return ResponseEntity.ok(ApiResponse.success(missions, "스토어 미션 목록을 성공적으로 조회했습니다."));
        } catch (Exception e) {
            log.error("Error fetching store missions", e);
            throw e;
        }
    }

    @GetMapping("/reward/{rewardId}")
    public ResponseEntity<ApiResponse<List<StoreMissionQueryDto>>> getStoreMissionsByReward(@PathVariable String rewardId) {
        List<StoreMissionQueryDto> missions = queryService.findByRewardId(rewardId);
        return ResponseEntity.ok(ApiResponse.success(missions, "리워드 ID로 스토어 미션 목록을 성공적으로 조회했습니다."));
    }

    @GetMapping("/tags/{tag}")
    public ResponseEntity<ApiResponse<List<StoreMissionQueryDto>>> getStoreMissionsByTag(@PathVariable String tag) {
        List<StoreMissionQueryDto> missions = queryService.findByTag(tag);
        return ResponseEntity.ok(ApiResponse.success(missions, "태그로 스토어 미션 목록을 성공적으로 조회했습니다."));
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<StoreMissionQueryDto>> getStoreMissionForEdit(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(queryService.findByIdForEdit(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<StoreMissionResponse>> updateStoreMission(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStoreMissionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            log.debug("Updating store mission with id: {} and request: {}", id, request);
            StoreMissionResponse response = commandService.updateStoreMission(id, request, userDetails.getMember().getId());
            return ResponseEntity.ok(ApiResponse.success(response, "리워드가 성공적으로 수정되었습니다."));
        } catch (Exception e) {
            log.error("Error updating store mission", e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteStoreMission(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            log.debug("Deleting store mission with id: {}", id);
            commandService.deleteStoreMission(id, userDetails.getMember().getId());
            return ResponseEntity.ok(ApiResponse.success(null, "리워드가 성공적으로 삭제되었습니다."));
        } catch (Exception e) {
            log.error("Error deleting store mission", e);
            throw e;
        }
    }

   
}
