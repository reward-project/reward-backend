package com.outsider.reward.global.infra.storage.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.outsider.reward.global.infra.storage.FileUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "cloud.aws", name = "enabled", havingValue = "true", matchIfMissing = false)
public class S3FileUploader implements FileUploader {

    private final AmazonS3Client amazonS3Client;
    
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    
    @Override
    public String upload(MultipartFile file, String dirPath) throws IOException {
        String fileName = createFileName(file.getOriginalFilename());
        String fileKey = dirPath + "/" + fileName;
        
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileKey, file.getInputStream(), metadata));
        return amazonS3Client.getUrl(bucket, fileKey).toString();
    }

    @Override
    public void delete(String fileUrl) {
        String fileKey = fileUrl.substring(fileUrl.indexOf(bucket) + bucket.length() + 1);
        amazonS3Client.deleteObject(bucket, fileKey);
    }
    
    private String createFileName(String originalFileName) {
        return UUID.randomUUID().toString() + getFileExtension(originalFileName);
    }
    
    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
} 