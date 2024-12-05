package com.outsider.reward.domain.store.presentation;

import com.outsider.reward.domain.store.command.application.StoreMissionCommandService;
import com.outsider.reward.domain.store.command.dto.CreateStoreMissionRequest;
import com.outsider.reward.domain.store.command.dto.StoreMissionResponse;
import com.outsider.reward.domain.store.command.mapper.StoreMissionMapper;
import com.outsider.reward.domain.store.query.application.StoreMissionQueryService;
import com.outsider.reward.domain.store.query.dto.StoreMissionQueryDto;
import com.outsider.reward.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<StoreMissionResponse> createStoreMission(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateStoreMissionRequest request) {
        try {
            log.debug("Creating store mission with request: {}", request);
            CreateStoreMissionRequest requestWithUserId = storeMissionMapper.toRequestWithUserId(request, userDetails.getUsername());
            StoreMissionResponse response = commandService.createStoreMission(requestWithUserId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating store mission", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreMissionQueryDto> getStoreMission(@PathVariable Long id) {
        return queryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/registrant/{registrantId}")
    public ResponseEntity<List<StoreMissionQueryDto>> getStoreMissionsByRegistrant(@PathVariable String registrantId) {
        List<StoreMissionQueryDto> missions = queryService.findByRegistrantId(registrantId);
        return ResponseEntity.ok(missions);
    }

    @GetMapping("/reward/{rewardId}")
    public ResponseEntity<List<StoreMissionQueryDto>> getStoreMissionsByReward(@PathVariable String rewardId) {
        List<StoreMissionQueryDto> missions = queryService.findByRewardId(rewardId);
        return ResponseEntity.ok(missions);
    }
}
