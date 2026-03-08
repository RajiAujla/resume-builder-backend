package com.project.resume_backend.controller;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.project.resume_backend.service.S3Service;

@RestController
public class S3Controller {
    @Autowired
    private S3Service s3Service;

    @GetMapping("/")
    public String hello(){
       return "Spring Boot applictaion is running";
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException{
        s3Service.uploadFile(file);
        return ResponseEntity.ok("File Uploaded successfully");
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable String filename){
        byte[] data = s3Service.downloadFile(filename);
        return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+filename+"\"")
                    .body(data);
    }
}
