package com.crg.mailservice.controller;



import com.crg.mailservice.model.Contact;
import com.crg.mailservice.service.EmailService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class BulkEmailController {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/send-with-attachment", consumes = {"multipart/form-data"})
    public ResponseEntity<String> sendEmailToMultipleUsersWithAttachment(
            @RequestPart("data") String requestJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        try {
            BulkEmailRequest request = objectMapper.readValue(requestJson, BulkEmailRequest.class);

            for (String email : request.getEmails()) {
                Contact contact = new Contact();
                contact.setEmail(email);
                contact.setName(""); // Optional
                emailService.sendEmailWithAttachment(contact, request.getSubject(), request.getBody(), file);
            }

            return ResponseEntity.ok("Emails sent successfully.");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Invalid JSON data: " + e.getMessage());
        }
    }
}








@JsonIgnoreProperties(ignoreUnknown = true)
@Data
class BulkEmailRequest {
    private String subject;
    private String body; // HTML body
    private List<String> emails;
}


