package com.outsider.reward.global.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outsider.reward.global.security.jwt.JwtTokenProvider;
import com.outsider.reward.domain.member.command.dto.TokenDto;
import com.outsider.reward.global.i18n.MessageUtils;
import com.outsider.reward.global.common.response.ApiResponse;
import com.outsider.reward.global.config.AppConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.i18n.LocaleContextHolder;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final MessageUtils messageUtils;
    private final AppConfig appConfig;

    @Override 
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getEmail();
        
        String accessToken = jwtTokenProvider.createToken(email);
        String refreshToken = jwtTokenProvider.createRefreshToken(email);
        
        String platform = request.getParameter("platform");
        String appType = request.getParameter("app_type");
        boolean isNativeApp = "android".equals(platform) || "ios".equals(platform);

        if (isNativeApp) {
            handleNativeAppResponse(response, accessToken, refreshToken);
        } else {
            handleWebResponse(response, accessToken, refreshToken, platform, appType);
        }
    }

    private void handleNativeAppResponse(HttpServletResponse response, String accessToken, String refreshToken) 
            throws IOException {
        TokenDto tokenDto = new TokenDto(accessToken, refreshToken);
        ApiResponse<TokenDto> apiResponse = ApiResponse.success(
            tokenDto, 
            messageUtils.getMessage("success.oauth2.login")
        );
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

    private void handleWebResponse(HttpServletResponse response, String accessToken, String refreshToken,
            String platform, String appType) throws IOException {
        
        // 플랫폼과 앱 타입에 따른 리다이렉트 URI 결정
        String redirectUri;
        if ("web".equals(platform)) {
            redirectUri = "app".equals(appType) 
                ? "https://app.reward-factory.shop/auth/callback"
                : "https://business.reward-factory.shop/auth/callback";
        } else {
            // 데스크톱 플랫폼 - 커스텀 URL 스킴으로 리다이렉트
            redirectUri = UriComponentsBuilder
                .fromUriString("reward-app://auth/callback")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build()
                .toUriString();
        }

        if ("desktop".equals(platform)) {
            // 데스크톱의 경우 쿠키 없이 바로 리다이렉트
            response.sendRedirect(redirectUri);
            return;
        }

        // 웹의 경우 기존 쿠키 처리 로직
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
            .path("/")
            .domain(appConfig.getCookie().getDomain())
            .secure(appConfig.getCookie().isSecure())
            .sameSite(appConfig.getCookie().getSameSite())
            .httpOnly(false)
            .maxAge(Duration.ofHours(1))
            .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
            .path("/")
            .domain(appConfig.getCookie().getDomain())
            .secure(appConfig.getCookie().isSecure())
            .sameSite(appConfig.getCookie().getSameSite())
            .httpOnly(false)
            .maxAge(Duration.ofDays(14))
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        response.sendRedirect(redirectUri);
    }
} 