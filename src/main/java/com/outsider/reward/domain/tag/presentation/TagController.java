package com.outsider.reward.domain.tag.presentation;

import com.outsider.reward.domain.tag.command.application.TagService;
import com.outsider.reward.global.common.response.ApiResponse;
import com.outsider.reward.global.security.CustomUserDetails;
import com.outsider.reward.domain.tag.command.dto.ShareTagRequest;
import com.outsider.reward.domain.tag.command.domain.TagSharePermission;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
public class TagController {
    private final TagService tagService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<String>>> searchTags(
            @RequestParam String query,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 인증되지 않은 사용자는 공개 태그만 검색
        if (userDetails == null) {
            return ResponseEntity.ok(ApiResponse.success(tagService.searchPublicTags(query)));
        }
        
        // 인증된 사용자는 공개 태그 + 자신의 태그 검색
        List<String> tags = tagService.searchTags(query, userDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.success(tags));
    }

    @GetMapping("/search/private")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<String>>> searchPrivateTags(
            @RequestParam String query,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<String> tags = tagService.searchPrivateTags(query, userDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.success(tags));
    }

    @GetMapping("/search/public")
    public ResponseEntity<ApiResponse<List<String>>> searchPublicTags(
            @RequestParam String query) {
        return ResponseEntity.ok(ApiResponse.success(tagService.searchPublicTags(query)));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<String>>> getMyTags(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<String> tags = tagService.getMyTags(userDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.success(tags));
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<String>>> getPopularTags() {
        return ResponseEntity.ok(ApiResponse.success(tagService.getPopularTags()));
    }

    @PostMapping("/{tagId}/share")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> shareTag(
            @PathVariable Long tagId,
            @RequestBody ShareTagRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        tagService.shareTag(tagId, request.getSharedWithId(), request.getPermission(), userDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.success("태그가 성공적으로 공유되었습니다."));
    }
} 