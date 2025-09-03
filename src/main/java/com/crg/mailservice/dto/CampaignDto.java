package com.crg.mailservice.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CampaignDto {
    private String title;
    private String subject;
    private Long templateId;
    private Long mailingListId; // ✅ Reference to MailingList
    private LocalDateTime scheduledAt;

    // Optional fields if you want to send these back in responses
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
