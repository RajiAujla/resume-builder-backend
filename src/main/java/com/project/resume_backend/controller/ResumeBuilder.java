package com.project.resume_backend.controller;

import java.io.*;
import java.nio.file.Files;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.resume_backend.model.ResumeRequest;

@RestController
@RequestMapping("/api/resume")
public class ResumeBuilder {

    private final RestTemplate restTemplate;
    private final String pdflatexPath;

    public ResumeBuilder(RestTemplate restTemplate, String pdflatexPath) { 
        this.restTemplate = restTemplate;
        this.pdflatexPath = pdflatexPath;
    }

 @PostMapping("/generate")
public String generateResumePdf(@RequestBody ResumeRequest request) throws IOException, InterruptedException {
    // Call Python FastAPI
    String aiResponse = restTemplate.postForObject(
            "http://localhost:8000/generate",
            request,
            String.class
    );
    //ResponseEntity<byte[]> response = generateLatexPdfResume(aiResponse);
    return aiResponse;   
}

    private ResponseEntity<byte[]> generateLatexPdfResume(String aiResponse) throws IOException, InterruptedException{
         ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.readTree(aiResponse);
    String latexContent = node.get("latex").asText(); 
    // Save LaTeX content to temp file
    File tempTexFile = File.createTempFile("resume", ".tex");
    try (FileWriter writer = new FileWriter(tempTexFile)) {
        writer.write(latexContent);
    }

    // Prepare ProcessBuilder
    ProcessBuilder pb = new ProcessBuilder(pdflatexPath, tempTexFile.getAbsolutePath());
    pb.directory(tempTexFile.getParentFile());

    Process process = pb.start();

    // Consume stdout in a separate thread
    Thread stdoutThread = new Thread(() -> {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    });

    // Consume stderr in a separate thread
    Thread stderrThread = new Thread(() -> {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.err.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    });

    stdoutThread.start();
    stderrThread.start();

    // Wait for both threads to finish
    stdoutThread.join();
    stderrThread.join();

    // Wait for pdflatex to exit
    int exitCode = process.waitFor();
    if (exitCode != 0) {
        throw new RuntimeException("pdflatex failed with exit code " + exitCode);
    }

    // Read PDF
    File pdfFile = new File(tempTexFile.getParent(), tempTexFile.getName().replace(".tex", ".pdf"));
    byte[] pdfBytes = Files.readAllBytes(pdfFile.toPath());

    // Return PDF
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("filename", "resume.pdf");

    // Clean temp files
    tempTexFile.delete();
    new File(pdfFile.getAbsolutePath().replace(".pdf", ".aux")).delete();
    new File(pdfFile.getAbsolutePath().replace(".pdf", ".log")).delete();

    return ResponseEntity.ok().headers(headers).body(pdfBytes);

    }
}