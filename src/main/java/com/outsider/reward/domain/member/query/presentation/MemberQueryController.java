package com.outsider.reward.domain.member.query.presentation;

import com.outsider.reward.domain.member.query.application.MemberQueryService;
import com.outsider.reward.domain.member.query.dto.MemberPointResponse;
import com.outsider.reward.domain.member.query.dto.CashHistoryResponse;
import com.outsider.reward.global.common.response.ApiResponse;
import com.outsider.reward.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberQueryController {

    private final MemberQueryService memberQueryService;

    @GetMapping("/me/point")
    public ApiResponse<MemberPointResponse> getPoint(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(memberQueryService.getPoint(userDetails.getId()));
    }

    @GetMapping("/me/cash-history")
    public ApiResponse<List<CashHistoryResponse>> getCashHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(memberQueryService.getCashHistory(userDetails.getId()));
    }
}
