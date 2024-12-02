package com.outsider.reward.global.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outsider.reward.global.common.response.ApiResponse;
import com.outsider.reward.global.i18n.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;
    private final MessageUtils messageUtils;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<?> apiResponse = ApiResponse.error(
            null,
            messageUtils.getMessage("error.oauth2.login.failed")
        );

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
} 