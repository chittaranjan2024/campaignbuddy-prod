package com.crg.mailservice.repository;

import com.crg.mailservice.model.Campaign;
import com.crg.mailservice.model.CampaignStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

   

    // Find campaigns that are scheduled but not yet sent
	List<Campaign> findByStatusAndScheduledAtBefore(CampaignStatus status, LocalDateTime time);
    // Find campaigns by status (DRAFT, SCHEDULED, SENT, FAILED, CANCELLED)
    List<Campaign> findByStatus(String status);

    // Optional: Search by title (case-insensitive)
    List<Campaign> findByTitleContainingIgnoreCase(String title);
    
 // If createdBy is a User entity
    List<Campaign> findByCreatedById(Long userId);
    
    

}
