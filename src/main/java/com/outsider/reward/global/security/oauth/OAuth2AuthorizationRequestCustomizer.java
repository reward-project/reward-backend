package com.outsider.reward.global.security.oauth;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Component
public class OAuth2AuthorizationRequestCustomizer implements OAuth2AuthorizationRequestResolver {
    
    private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;
    
    public OAuth2AuthorizationRequestCustomizer(ClientRegistrationRepository clientRegistrationRepository) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(
            clientRegistrationRepository, 
            "/oauth2/authorization"
        );
    }
    
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authRequest = defaultResolver.resolve(request);
        if (authRequest == null) {
            return null;
        }
        
        return customizeAuthorizationRequest(authRequest, request);
    }
    
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authRequest = defaultResolver.resolve(request, clientRegistrationId);
        if (authRequest == null) {
            return null;
        }
        
        return customizeAuthorizationRequest(authRequest, request);
    }
    
    private OAuth2AuthorizationRequest customizeAuthorizationRequest(
            OAuth2AuthorizationRequest authRequest, 
            HttpServletRequest request) {
            
        Map<String, Object> additionalParameters = new HashMap<>(
            authRequest.getAdditionalParameters()
        );
        
        // URL 파라미터에서 platform과 role 정보 가져오기
        String platform = request.getParameter("platform");
        String role = request.getParameter("role");
        
        if (platform != null) {
            additionalParameters.put("platform", platform);
        }
        if (role != null) {
            additionalParameters.put("role", role);
        }
        
        return OAuth2AuthorizationRequest.from(authRequest)
            .additionalParameters(additionalParameters)
            .build();
    }
} 