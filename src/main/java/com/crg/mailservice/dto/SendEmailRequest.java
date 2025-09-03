package com.crg.mailservice.dto;

import java.util.List;

public record SendEmailRequest(
    Long campaignId,
    List<String> recipients, // list of email addresses
    String subject,
    String body,
    boolean schedule, // true if sending later
    String scheduleTime // Optional: ISO format - "2025-08-06T17:00:00"
) {}
