package com.crg.mailservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampaignActivityDto {
    private Long id;
    private String campaignTitle;
    private String recipient;
    private boolean delivered;
    private boolean opened;
    private boolean clicked;
    private LocalDateTime timestamp;
}
