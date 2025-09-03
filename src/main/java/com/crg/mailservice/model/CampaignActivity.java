package com.crg.mailservice.model;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampaignActivity {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Campaign campaign;
    private String recipient;
    private boolean delivered;
    private boolean opened;
    private boolean clicked;
    private LocalDateTime timestamp;
}

