package com.project.resume_backend.model;
import lombok.Data;
import java.util.List;

@Data
public class ResumeRequest {
    private String name;
    private String experience;
    private List<String> skills;
}
