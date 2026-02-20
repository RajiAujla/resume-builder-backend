package com.project.resume_backend;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.resume_backend.controller.ResumeBuilder;
import com.project.resume_backend.model.ResumeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(ResumeBuilder.class)
class ResumeBuilderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGenerateResumeEndpoint() throws Exception {
        ResumeRequest request = new ResumeRequest();
        request.setName("Rajwinder Kaur");
        request.setExperience("Java, Spring Boot");
        request.setSkills(java.util.Arrays.asList("REST API", "SQL"));

        String mockResponse = "{\"latex\":\"mock LaTeX\"}";
        when(restTemplate.postForObject(anyString(), any(), eq(String.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/resume/generate")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }
}
