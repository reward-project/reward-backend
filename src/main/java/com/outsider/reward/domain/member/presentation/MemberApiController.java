package com.outsider.reward.domain.member.presentation;

import com.outsider.reward.domain.member.command.application.EmailService;
import com.outsider.reward.domain.member.command.application.MemberCommandService;
import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.dto.MemberCommand;
import com.outsider.reward.domain.member.command.dto.TokenDto;
import com.outsider.reward.domain.member.command.dto.TokenRefreshRequest;
import com.outsider.reward.domain.member.mapper.MemberMapper;
import com.outsider.reward.domain.member.command.dto.GoogleCallbackRequest;
import com.outsider.reward.domain.member.query.application.MemberQueryService;
import com.outsider.reward.domain.member.query.dto.MemberQuery;
import com.outsider.reward.domain.member.query.dto.MemberResponse;
import com.outsider.reward.global.security.CustomUserDetails;
import com.outsider.reward.global.security.jwt.JwtTokenProvider;
import com.outsider.reward.global.common.exception.BusinessException;
import com.outsider.reward.global.common.response.ApiResponse;
import com.outsider.reward.global.i18n.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 관련 API")
public class MemberApiController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;
    private final EmailService emailService;
    private final MessageUtils messageUtils;
    private final MemberMapper memberMapper;
    private final JwtTokenProvider jwtTokenProvider;
    @Operation(summary = "일반 사용자 회원가입", description = "앱 사용자용 회원가입을 처리합니다.")
    @PostMapping("/signup/user")
    public ResponseEntity<ApiResponse<Long>> signUpUser(@Valid @RequestBody MemberCommand.SignUp command) {
        Long memberId = memberCommandService.signUpUser(command);
        return ResponseEntity.ok(ApiResponse.success(memberId, messageUtils.getMessage("success.signup")));
    }

    @Operation(summary = "비즈니스 회원가입", description = "비즈니스 관계자용 회원가입을 처리합니다.")
    @PostMapping("/signup/business")
    public ResponseEntity<ApiResponse<Long>> signUpBusiness(@Valid @RequestBody MemberCommand.SignUp command) {
        Long memberId = memberCommandService.signUpBusiness(command);
        return ResponseEntity.ok(ApiResponse.success(memberId, messageUtils.getMessage("success.signup")));
    }

    @Operation(summary = "관리자 회원가입", description = "관리자용 회원가입을 처리합니다.")
    @PostMapping("/signup/admin")
    public ResponseEntity<ApiResponse<Long>> signUpAdmin(@Valid @RequestBody MemberCommand.SignUp command) {
        Long memberId = memberCommandService.signUpAdmin(command);
        return ResponseEntity.ok(ApiResponse.success(memberId, messageUtils.getMessage("success.signup")));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenDto>> login(
            @RequestBody @Valid MemberCommand.Login command) {
        TokenDto tokenDto = memberCommandService.login(command);
        return ResponseEntity.ok(ApiResponse.success(tokenDto, 
            messageUtils.getMessage("success.login")));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberQuery.MemberInfo>> getMember(@PathVariable Long id) {
        MemberQuery.MemberInfo memberInfo = memberQueryService.getMemberInfo(id);
        return ResponseEntity.ok(ApiResponse.success(memberInfo));
    }
    
    @PostMapping("/verify/send")
    public ResponseEntity<ApiResponse<Void>> sendVerificationEmail(@RequestParam String email) {
        try {
            emailService.sendVerificationEmail(email);
            return ResponseEntity.ok(ApiResponse.success(null, 
                messageUtils.getMessage("success.email.verification.sent")));
        } catch (BusinessException e) {
            return ResponseEntity.ok(ApiResponse.error(null, messageUtils.getMessage(e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(null, messageUtils.getMessage("error.email.send.fail")));
        }
    }
    
    @PostMapping("/verify/check")
    public ResponseEntity<ApiResponse<Boolean>> verifyEmail(@RequestParam String email, @RequestParam String code) {
        boolean isVerified = emailService.verifyEmail(email, code);
        String message = isVerified ? 
            messageUtils.getMessage("success.email.verified") : 
            messageUtils.getMessage("error.invalid.verification.code");
        return ResponseEntity.ok(ApiResponse.success(isVerified, message));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenDto>> refreshToken(@RequestBody TokenRefreshRequest request) {
        log.debug("Token refresh request received with token: {}", request.getRefreshToken().substring(0, 10) + "...");
        try {
            TokenDto tokenDto = jwtTokenProvider.refreshAccessToken(request.getRefreshToken());
            log.debug("Token refresh successful");
            return ResponseEntity.ok(ApiResponse.success(tokenDto));
        } catch (Exception e) {
            log.error("Token refresh failed: ", e);
            throw e;
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody(required = false) TokenRefreshRequest request) {
        
        if (request != null && request.getRefreshToken() != null) {
            memberCommandService.logout(request.getRefreshToken(), userDetails.getUsername());
        } else {
            memberCommandService.logoutAll(userDetails.getUsername());
        }
        
        return ResponseEntity.ok(ApiResponse.success(null, 
            messageUtils.getMessage("success.logout")));
    }
    
    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse<Void>> logoutAll(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 모든 기기에서 로그아웃
        memberCommandService.logoutAll(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null, 
            messageUtils.getMessage("success.logout.all")));
    }
    
    @PutMapping("/{id}/profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(@PathVariable Long id, @Valid @RequestBody MemberCommand.UpdateProfile command) {
        memberCommandService.updateProfile(id, command);
        return ResponseEntity.ok(ApiResponse.success(null, "프로필이 수정되었습니다."));
    }
    
    @PostMapping("/{id}/profile-image")
    public ResponseEntity<ApiResponse<String>> updateProfileImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        String imageUrl = memberCommandService.updateProfileImage(id, file);
        return ResponseEntity.ok(ApiResponse.success(imageUrl, "프로필 이미지가 업로드되었습니다."));
    }
    
    @Operation(summary = "구글 OAuth2 콜백", description = "구글 로그인 콜백을 처리합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 토큰")
    })
    @PostMapping("/oauth2/google/callback")
    public ResponseEntity<ApiResponse<TokenDto>> handleGoogleCallback(
            @RequestBody GoogleCallbackRequest request) {
        TokenDto tokenDto = memberCommandService.handleGoogleCallback(
            request.getIdToken(), 
            request.getRole()
        );
        return ResponseEntity.ok(ApiResponse.success(tokenDto));
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponse>> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = memberQueryService.getMemberByEmail(userDetails.getUsername());
        MemberResponse response = memberMapper.toResponse(member);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
} 