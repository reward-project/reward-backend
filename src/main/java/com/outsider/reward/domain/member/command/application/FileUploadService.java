package com.outsider.reward.domain.member.command.application;

import com.outsider.reward.global.infra.storage.FileUploader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileUploadService {

    private final FileUploader fileUploader;
    
    public FileUploadService(@Qualifier("minioFileUploader") FileUploader fileUploader) {
        this.fileUploader = fileUploader;
    }
    
    public String uploadFile(MultipartFile file) throws IOException {
        return fileUploader.upload(file, "profiles");
    }
    
    public void deleteFile(String fileUrl) throws IOException {
        fileUploader.delete(fileUrl);
    }
} 