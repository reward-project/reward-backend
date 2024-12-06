package com.outsider.reward.domain.platform.presentation;

import com.outsider.reward.domain.platform.command.application.PlatformService;
import com.outsider.reward.domain.platform.command.dto.AddPlatformDomainRequest;
import com.outsider.reward.domain.platform.command.dto.CreatePlatformRequest;
import com.outsider.reward.domain.platform.command.dto.PlatformResponse;
import com.outsider.reward.domain.platform.command.dto.PlatformDomainResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/platforms")
public class PlatformController {
    private final PlatformService platformService;

    @PostMapping
    public ResponseEntity<PlatformResponse> createPlatform(
            @Valid @RequestBody CreatePlatformRequest request
    ) {
        return ResponseEntity.ok(platformService.createPlatform(request));
    }

    @PostMapping("/{platformId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlatformResponse> approvePlatform(
            @PathVariable Long platformId
    ) {
        return ResponseEntity.ok(platformService.approvePlatform(platformId));
    }

    @PostMapping("/{platformId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlatformResponse> rejectPlatform(
            @PathVariable Long platformId
    ) {
        return ResponseEntity.ok(platformService.rejectPlatform(platformId));
    }

    @PostMapping("/{platformId}/domains")
    public ResponseEntity<Void> addDomain(
            @PathVariable Long platformId,
            @Valid @RequestBody AddPlatformDomainRequest request
    ) {
        platformService.addDomain(platformId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{platformId}/domains")
    public ResponseEntity<List<PlatformDomainResponse>> getPlatformDomains(
            @PathVariable Long platformId
    ) {
        return ResponseEntity.ok(platformService.getPlatformDomains(platformId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<PlatformResponse>> getActivePlatforms() {
        return ResponseEntity.ok(platformService.getActivePlatforms());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PlatformResponse>> getPendingPlatforms() {
        return ResponseEntity.ok(platformService.getPendingPlatforms());
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlatformResponse>> searchPlatforms(
            @RequestParam String searchTerm
    ) {
        return ResponseEntity.ok(platformService.searchPlatforms(searchTerm));
    }

    @GetMapping("/domains/check")
    public ResponseEntity<Map<String, Boolean>> checkDomainAvailability(@RequestParam String domain) {
        boolean isAvailable = platformService.isDomainAvailable(domain);
        return ResponseEntity.ok(Map.of("available", isAvailable));
    }
}
