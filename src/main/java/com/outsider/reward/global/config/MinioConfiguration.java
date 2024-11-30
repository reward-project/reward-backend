package com.outsider.reward.global.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {
    
    private final MinioConfig minioConfig;

    public MinioConfiguration(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }

    @Bean(name = "minioClient")
    public MinioClient minioClient() {
        return MinioClient.builder()
            .endpoint(minioConfig.getUrl())
            .credentials(minioConfig.getAccessKey(), minioConfig.getSecretKey())
            .build();
    }
} 