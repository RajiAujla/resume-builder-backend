package com.project.resume_backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.resume_backend.model.ResumeRequest;

@RestController
@RequestMapping("/api/resume")
public class ResumeBuilder {
    
    @PostMapping("/generate")
    public String generateResume(@RequestBody ResumeRequest request) {
        return "Resume generation started";
    }
}
