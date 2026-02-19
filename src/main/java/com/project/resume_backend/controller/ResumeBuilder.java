package com.project.resume_backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.project.resume_backend.model.ResumeRequest;

@RestController
@RequestMapping("/api/resume")
public class ResumeBuilder {
    
    @PostMapping("/generate")
    public String generateResume(@RequestBody ResumeRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        String aiResponse = restTemplate.postForObject(
        "http://localhost:8000/generate",
        request,
        String.class
        );
        return aiResponse;
    }
}
