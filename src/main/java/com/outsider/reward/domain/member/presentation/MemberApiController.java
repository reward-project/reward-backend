package com.outsider.reward.domain.member.presentation;

import com.outsider.reward.domain.member.command.application.EmailService;
import com.outsider.reward.domain.member.command.application.MemberCommandService;
import com.outsider.reward.domain.member.command.domain.Member;
import com.outsider.reward.domain.member.command.dto.MemberCommand;
import com.outsider.reward.domain.member.command.dto.TokenDto;
import com.outsider.reward.domain.member.mapper.MemberMapper;
import com.outsider.reward.domain.member.command.dto.GoogleCallbackRequest;
import com.outsider.reward.domain.member.query.application.MemberQueryService;
import com.outsider.reward.domain.member.query.dto.MemberQuery;
import com.outsider.reward.domain.member.query.dto.MemberResponse;
import com.outsider.reward.global.security.CustomUserDetails;
import com.outsider.reward.global.common.exception.BusinessException;
import com.outsider.reward.global.common.response.ApiResponse;
import com.outsider.reward.global.i18n.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
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
    
    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "중복된 이메일 또는 닉네임")
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Long>> signUp(@Valid @RequestBody MemberCommand.SignUp command) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        log.info("Current Locale: {}", currentLocale);
        log.info("Accept-Language Header: {}", LocaleContextHolder.getLocaleContext());
        
        Long memberId = memberCommandService.signUp(command);
        return ResponseEntity.ok(ApiResponse.success(memberId, messageUtils.getMessage("success.signup")));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MemberCommand.LoginResponse>> login(
            @RequestBody @Valid MemberCommand.Login command
           ) {

        MemberCommand.LoginResponse response = memberCommandService.login(command);
        String message = messageUtils.getMessage("success.login");
        
        return ResponseEntity.ok(ApiResponse.success(response, message));
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
    public ResponseEntity<ApiResponse<TokenDto>> refresh(@RequestParam String refreshToken) {
        TokenDto tokenDto = memberCommandService.refresh(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(tokenDto));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        memberCommandService.logout(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null, messageUtils.getMessage("success.logout")));
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
    public ResponseEntity<ApiResponse<TokenDto>> googleCallback(
            @RequestBody @Valid GoogleCallbackRequest request) {
        log.debug("Received Google callback request with token length: {}", 
            request.getIdToken().length());
        
        TokenDto tokenDto = memberCommandService.handleGoogleCallback(request.getIdToken());
        return ResponseEntity.ok(ApiResponse.success(tokenDto, 
            messageUtils.getMessage("success.oauth2.login")));
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponse>> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = memberQueryService.getMemberByEmail(userDetails.getUsername());
        MemberResponse response = memberMapper.toResponse(member);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
} 