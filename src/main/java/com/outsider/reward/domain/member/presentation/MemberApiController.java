package com.outsider.reward.domain.member.presentation;

import com.outsider.reward.common.response.ApiResponse;
import com.outsider.reward.domain.member.command.application.MemberCommandService;
import com.outsider.reward.domain.member.command.dto.MemberCommand;
import com.outsider.reward.domain.member.query.application.MemberQueryService;
import com.outsider.reward.domain.member.query.dto.MemberQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;
    
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Long>> signUp(@Valid @RequestBody MemberCommand.SignUp command) {
        Long memberId = memberCommandService.signUp(command);
        return ResponseEntity.ok(ApiResponse.success(memberId, "회원가입이 완료되었습니다."));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MemberCommand.LoginResponse>> login(
            @Valid @RequestBody MemberCommand.Login command) {
        MemberCommand.LoginResponse response = memberCommandService.login(command);
        return ResponseEntity.ok(ApiResponse.success(response, "로그인이 완료되었습니다."));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberQuery.MemberInfo>> getMember(@PathVariable Long id) {
        MemberQuery.MemberInfo memberInfo = memberQueryService.getMemberInfo(id);
        return ResponseEntity.ok(ApiResponse.success(memberInfo));
    }
    
    @PostMapping("/verify/send")
    public ResponseEntity<ApiResponse<Void>> sendVerificationEmail(@RequestParam String email) {
        emailService.sendVerificationEmail(email);
        return ResponseEntity.ok(ApiResponse.success(null, "인증 코드가 발송되었습니다."));
    }
    
    @PostMapping("/verify/check")
    public ResponseEntity<ApiResponse<Boolean>> verifyEmail(
            @RequestParam String email,
            @RequestParam String code) {
        boolean isVerified = emailService.verifyEmail(email, code);
        return ResponseEntity.ok(ApiResponse.success(isVerified,
                isVerified ? "인증이 완료되었습니다." : "인증에 실패했습니다."));
    }
} 