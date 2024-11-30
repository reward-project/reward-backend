package com.outsider.reward.global.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outsider.reward.global.security.jwt.JwtTokenProvider;
import com.outsider.reward.domain.member.command.dto.TokenDto;
import com.outsider.reward.common.response.ApiResponse;
import com.outsider.reward.global.i18n.MessageUtils;
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
        
        String userAgent = request.getHeader("User-Agent");
        boolean isMobile = userAgent != null && 
            (userAgent.contains("Android") || userAgent.contains("iPhone") || userAgent.contains("iPad"));

        String locale = request.getParameter("locale");
        if (locale == null) {
            locale = "ko";
        }

        if (isMobile) {
            // 모바일용 응답 - 토큰만 전송
            TokenDto tokenDto = new TokenDto(accessToken, refreshToken);
            ApiResponse<TokenDto> apiResponse = ApiResponse.success(
                tokenDto, 
                messageUtils.getMessage("success.oauth2.login")
            );
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        } else {
            // 웹용 응답 - 쿠키 설정 및 리다이렉트
            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .path("/")
                .domain(appConfig.getCookie().getDomain())
                .secure(appConfig.getCookie().isSecure())
                .sameSite(appConfig.getCookie().getSameSite())
                .httpOnly(true)
                .maxAge(Duration.ofHours(1))
                .build();

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .path("/")
                .domain(appConfig.getCookie().getDomain())
                .secure(appConfig.getCookie().isSecure())
                .sameSite(appConfig.getCookie().getSameSite())
                .httpOnly(true)
                .maxAge(Duration.ofDays(14))
                .build();

            response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            // 리다이렉트 URL 생성 및 리다이렉트
            String redirectUrl = UriComponentsBuilder
                .fromUriString(appConfig.getFrontend().getUrl())
                .fragment("/" + locale + "/auth/callback")
                .build().toUriString();

            response.sendRedirect(redirectUrl);
        }
    }
} 