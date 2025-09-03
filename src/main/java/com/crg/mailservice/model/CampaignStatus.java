package com.crg.mailservice.model;

public enum CampaignStatus {
    DRAFT,      // Campaign created but not scheduled
    SCHEDULED,  // Campaign scheduled for future delivery
    SENT,       // Campaign sent successfully
    FAILED,     // Optional - in case of error
    CANCELED    // Optional - if user cancels before sending
}
