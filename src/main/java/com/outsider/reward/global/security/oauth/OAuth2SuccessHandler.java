package com.outsider.reward.global.security.oauth;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outsider.reward.domain.member.command.dto.TokenDto;
import com.outsider.reward.global.common.response.ApiResponse;
import com.outsider.reward.global.config.AppConfig;
import com.outsider.reward.global.i18n.MessageUtils;
import com.outsider.reward.global.security.jwt.JwtTokenProvider;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        
        String platform = getCookieValue(request, "platform");
        String role = getCookieValue(request, "role");

        log.info("OAuth2 Success - Email: {}, Platform: {}, Role: {}", email, platform, role);

        if (platform == null) {
            platform = "web";
        }
        if (role == null) {
            role = "user";
        }

        String locale = LocaleContextHolder.getLocale().getLanguage();
        
        boolean isNativeApp = "android".equals(platform) || "ios".equals(platform);

        if (isNativeApp) {
            handleNativeAppResponse(response, accessToken, refreshToken);
        } else {
            handleWebResponse(response, accessToken, refreshToken, platform, role, locale);
        }
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
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
            String platform, String role, String locale) throws IOException {
        
        String redirectUri;
        boolean isDev = Arrays.asList(environment.getActiveProfiles()).contains("dev");
        
        if ("web".equals(platform)) {
            if (isDev) {
                redirectUri = "user".equals(role)
                    ? String.format("http://localhost:46151/#/%s/auth/callback", locale)
                    : String.format("http://localhost:46151/#/%s/auth/callback", locale);
            } else {
                redirectUri = "user".equals(role)
                    ? String.format("https://app.reward-factory.shop/#/%s/auth/callback", locale)
                    : String.format("https://business.reward-factory.shop/#/%s/auth/callback", locale);
            }
        } else if ("desktop".equals(platform)) {
            redirectUri = String.format("http://localhost:8765/auth/callback?accessToken=%s&refreshToken=%s",
                URLEncoder.encode(accessToken, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(refreshToken, StandardCharsets.UTF_8.toString()));
            
            log.info("Redirecting to desktop: {}", redirectUri);
        } else {
            redirectUri = isDev 
                ? String.format("http://localhost:46151/#/%s/auth/callback", locale)
                : String.format("https://app.reward-factory.shop/#/%s/auth/callback", locale);
        }

        if ("desktop".equals(platform)) {
            log.info("Redirecting to desktop: {}", redirectUri);
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