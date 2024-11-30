package com.outsider.reward.global.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "cloud.aws", name = "enabled", havingValue = "true", matchIfMissing = false)
public class S3Config {
    
    @Value("${cloud.aws.credentials.access-key:NONE}")
    private String accessKey;
    
    @Value("${cloud.aws.credentials.secret-key:NONE}")
    private String secretKey;
    
    @Value("${cloud.aws.region.static:ap-northeast-2}")
    private String region;
    
    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
} 