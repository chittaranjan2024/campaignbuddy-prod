package com.crg.mailservice.dto;

import lombok.Data;

@Data
public class TemplateDto {
    private Long id; // optional for update
    private String name;
    private String subject;
    private String body; // could be HTML
}

