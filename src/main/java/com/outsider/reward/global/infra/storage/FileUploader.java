package com.outsider.reward.global.infra.storage;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileUploader {
    String upload(MultipartFile file, String dirPath) throws IOException;
    void delete(String fileUrl) throws IOException;
} 