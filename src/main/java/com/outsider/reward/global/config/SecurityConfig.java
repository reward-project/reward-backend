package com.outsider.reward.global.config;

import com.outsider.reward.global.security.jwt.JwtAuthenticationFilter;
import com.outsider.reward.global.security.jwt.JwtTokenProvider;
import com.outsider.reward.global.security.oauth.GoogleOAuth2UserService;
import com.outsider.reward.global.security.oauth.OAuth2SuccessHandler;
import com.outsider.reward.global.security.oauth.OAuth2FailureHandler;
import lombok.RequiredArgsConstructor;
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
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import com.outsider.reward.global.security.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import com.outsider.reward.global.security.oauth.OAuth2AuthorizationRequestCustomizer;
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleOAuth2UserService googleOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final CorsConfigurationSource corsConfigurationSource;
    private final GetOrFullAuthorizationManager getOrFullAuthorizationManager;
    private final OAuth2AuthorizationRequestCustomizer oAuth2AuthorizationRequestCustomizer;

    private String[] permittedEndpoints() {
        return new String[] {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/api/v1/members/signup",
            "/api/v1/members/login",
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
                // .requestMatchers("/**").permitAll()
                .requestMatchers("/**").access(getOrFullAuthorizationManager)
                .anyRequest().authenticated()
            )
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorization -> authorization
                    .baseUri("/oauth2/authorization")
                    .authorizationRequestResolver(oAuth2AuthorizationRequestCustomizer)
                    .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                )
                .redirectionEndpoint(redirection -> redirection
                    .baseUri("/login/oauth2/code/*")
                )
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(googleOAuth2UserService)
                )
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler)
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                    UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }
} 