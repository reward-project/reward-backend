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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final MessageUtils messageUtils;
    private final AppConfig appConfig;
    private final Environment environment;

    @Override 
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getEmail();
        
        String accessToken = jwtTokenProvider.createToken(email);
        String refreshToken = jwtTokenProvider.createRefreshToken(email);
        
        String platform = request.getParameter("platform") != null ? 
            request.getParameter("platform") : "web";
        String appType = request.getParameter("app_type") != null ? 
            request.getParameter("app_type") : "app";
        
        String locale = LocaleContextHolder.getLocale().getLanguage();
        
        boolean isNativeApp = "android".equals(platform) || "ios".equals(platform);

        if (isNativeApp) {
            handleNativeAppResponse(response, accessToken, refreshToken);
        } else {
            handleWebResponse(response, accessToken, refreshToken, platform, appType, locale);
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
            String platform, String appType, String locale) throws IOException {
        
        String redirectUri;
        boolean isDev = Arrays.asList(environment.getActiveProfiles()).contains("dev");
        
        if ("web".equals(platform)) {
            if (isDev) {
                redirectUri = "app".equals(appType)
                    ? String.format("http://localhost:46151/#/%s/auth/callback", locale)
                    : String.format("http://localhost:46151/#/%s/auth/callback", locale);
            } else {
                redirectUri = "app".equals(appType)
                    ? String.format("https://app.reward-factory.shop/#/%s/auth/callback", locale)
                    : String.format("https://business.reward-factory.shop/#/%s/auth/callback", locale);
            }
        } else if ("desktop".equals(platform)) {
            redirectUri = UriComponentsBuilder
                .fromUriString("reward-app://auth/callback")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .queryParam("locale", locale)
                .build()
                .toUriString();
        } else {
            redirectUri = isDev 
                ? String.format("http://localhost:46151/#/%s/auth/callback", locale)
                : String.format("https://app.reward-factory.shop/#/%s/auth/callback", locale);
        }

        if ("desktop".equals(platform)) {
            response.sendRedirect(redirectUri);
            return;
        }

        // 웹의 경우 쿠키 설정
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