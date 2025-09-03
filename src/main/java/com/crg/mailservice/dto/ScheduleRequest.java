package com.crg.mailservice.dto;

import java.time.LocalDateTime;


import java.time.LocalDateTime;

public class ScheduleRequest {

    private LocalDateTime scheduledAt;

    public ScheduleRequest() {
    }

    public ScheduleRequest(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
}

