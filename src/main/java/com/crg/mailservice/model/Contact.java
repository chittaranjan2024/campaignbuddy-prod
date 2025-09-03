package com.crg.mailservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Contact {

	@EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String email;

    private String phone;

    private String company;

    private String designation;

    private String location;

    private String tags; // comma-separated tags for segmentation

    private boolean subscribed = true; // subscription status

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    private User owner;
}

