package com.crg.mailservice.dto;

import lombok.Data;

@Data
public class ContactDto {
    private Long id; // optional (used for update)
    private String name;
    private String email;
}
