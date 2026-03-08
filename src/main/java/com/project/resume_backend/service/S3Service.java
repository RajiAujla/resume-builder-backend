package com.project.resume_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.resume_backend.config.AppConfig;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {
    private final S3Client s3Client;
    private final String bucketName;
    private final String bucketFolder;

    public S3Service(S3Client s3Client, AppConfig appConfig) { 
        this.s3Client = s3Client;
        this.bucketName = appConfig.getBucketName();
        this.bucketFolder = appConfig.getBucketFolder();
    }

    public void uploadFile(MultipartFile file) throws IOException{
        String key = bucketFolder + "/" + file.getOriginalFilename();
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build(), 
                    RequestBody.fromBytes(file.getBytes()));
    }

    public byte[] downloadFile(String fileName){
        String key = bucketFolder + "/" + fileName;
        ResponseBytes<GetObjectResponse> objectAsBytes = s3Client.getObjectAsBytes(GetObjectRequest.builder()
                                .bucket(bucketName)
                                .key(key)
                                .build());
                    return objectAsBytes.asByteArray();
    }
}
