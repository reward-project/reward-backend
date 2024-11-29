package com.outsider.reward.global.infra.storage.minio;

import com.outsider.reward.global.infra.storage.FileUploader;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MinioFileUploader implements FileUploader {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.url}")
    private String minioUrl;

    @Override
    public String upload(MultipartFile file, String dirPath) throws IOException {
        try {
            String fileName = createFileName(file.getOriginalFilename());
            String objectName = dirPath + "/" + fileName;

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            return minioUrl + "/" + bucket + "/" + objectName;
        } catch (Exception e) {
            throw new IOException("Failed to upload file to MinIO", e);
        }
    }

    @Override
    public void delete(String fileUrl) throws IOException {
        try {
            String objectName = extractObjectNameFromUrl(fileUrl);
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new IOException("Failed to delete file from MinIO", e);
        }
    }

    private String createFileName(String originalFileName) {
        return UUID.randomUUID().toString() + getFileExtension(originalFileName);
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String extractObjectNameFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.indexOf(bucket) + bucket.length() + 1);
    }
} 