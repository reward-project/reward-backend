package com.outsider.reward.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "minio")
@Getter
@Setter
public class MinioConfig {
    private String url;
    private String accessKey;
    private String secretKey;
    private String bucket;
} 