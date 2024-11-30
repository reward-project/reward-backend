package com.outsider.reward.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppConfig {
    private Frontend frontend;
    private Cookie cookie;

    @Getter
    @Setter
    public static class Frontend {
        private String url;
    }

    @Getter
    @Setter
    public static class Cookie {
        private String domain;
        private boolean secure;
        private String sameSite;
    }
} 