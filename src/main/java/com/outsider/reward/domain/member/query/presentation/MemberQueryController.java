package com.outsider.reward.domain.member.query.presentation;

import com.outsider.reward.domain.member.query.application.MemberQueryService;
import com.outsider.reward.domain.member.query.dto.MemberPointResponse;
import com.outsider.reward.domain.member.query.dto.CashHistoryResponse;
import com.outsider.reward.global.common.response.ApiResponse;
import com.outsider.reward.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ApiResponse<Page<CashHistoryResponse>> getCashHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "ALL") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(memberQueryService.getCashHistory(
            userDetails.getId(), 
            type, 
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionDate"))));
    }
}
