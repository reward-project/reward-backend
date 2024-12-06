package com.outsider.reward.global.config;

import com.outsider.reward.global.security.jwt.JwtAuthenticationFilter;
import com.outsider.reward.global.security.jwt.JwtTokenProvider;
import com.outsider.reward.global.security.oauth.OAuth2SuccessHandler;
import com.outsider.reward.global.security.oauth.service.GoogleOAuth2UserService;
import com.outsider.reward.global.security.oauth.service.KakaoOAuth2UserService;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;
import org.springframework.web.util.CookieGenerator;
import jakarta.servlet.http.Cookie;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CorsConfigurationSource corsConfigurationSource;
    private final GetOrFullAuthorizationManager getOrFullAuthorizationManager;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final GoogleOAuth2UserService googleOAuth2UserService;
    private final KakaoOAuth2UserService kakaoOAuth2UserService;

    private String[] permittedEndpoints() {
        return new String[] {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/api/v1/members/signup/**",
            "/api/v1/members/login",
            "/api/v1/members/refresh",
            "/api/v1/members/verify/**",
            "/oauth2/authorization/**",
            "/login/oauth2/code/*",
            "/api/v1/members/oauth2/google/callback"
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.debug("Configuring SecurityFilterChain");
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .sessionManagement(sessionManagement -> 
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(permittedEndpoints()).permitAll()
                .requestMatchers("/**").access(getOrFullAuthorizationManager)
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"message\":\"Unauthorized\"}");
                })
            )
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorization -> authorization
                    .baseUri("/oauth2/authorization")
                    .authorizationRequestResolver(authorizationRequestResolver(clientRegistrationRepository))
                )
                .redirectionEndpoint(redirection -> redirection
                    .baseUri("/login/oauth2/code/*")
                )
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(request -> {
                        if (request.getClientRegistration().getRegistrationId().equals("kakao")) {
                            return kakaoOAuth2UserService.loadUser(request);
                        }
                        return googleOAuth2UserService.loadUser(request);
                    })
                )
                .successHandler(oAuth2SuccessHandler)
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                    UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
    @Bean
public OAuth2AuthorizationRequestResolver authorizationRequestResolver(
        ClientRegistrationRepository clientRegistrationRepository) {
    DefaultOAuth2AuthorizationRequestResolver defaultResolver =
        new DefaultOAuth2AuthorizationRequestResolver(
            clientRegistrationRepository, "/oauth2/authorization");
    
    return new OAuth2AuthorizationRequestResolver() {
        @Override
        public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
            OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
            if (req == null) return null;

            String platform = request.getParameter("platform");
            String role = request.getParameter("role");

            // Cookie에 platform과 role 저장
            Cookie platformCookie = new Cookie("platform", platform);
            platformCookie.setPath("/");
            platformCookie.setHttpOnly(true);
            platformCookie.setMaxAge(300); // 5분 동안 유효

            Cookie roleCookie = new Cookie("role", role);
            roleCookie.setPath("/");
            roleCookie.setHttpOnly(true);
            roleCookie.setMaxAge(300); // 5분 동안 유효

            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            response.addCookie(platformCookie);
            response.addCookie(roleCookie);

            log.info("OAuth2 Authorization Request - Platform: {}, Role: {}", platform, role);

            Map<String, Object> additionalParameters = new HashMap<>(req.getAdditionalParameters());
            additionalParameters.put("platform", platform);
            additionalParameters.put("role", role);

            String state = platform + ":" + role;
            return OAuth2AuthorizationRequest.from(req)
                .additionalParameters(additionalParameters)
                .state(state)
                .build();
        }

        @Override
        public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
            return resolve(request);
        }
    };
}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


} 