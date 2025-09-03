package com.crg.mailservice.controller;

import com.crg.mailservice.dto.ScheduleRequest;
import com.crg.mailservice.model.Campaign;
import com.crg.mailservice.model.CampaignStatus;
import com.crg.mailservice.repository.CampaignRepository;
import com.crg.mailservice.service.CampaignService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignRepository campaignRepository;
    private final CampaignService campaignService;

    // Create a campaign (default as DRAFT)
    @PostMapping
    public ResponseEntity<Campaign> createCampaign(@RequestBody Campaign campaign) {
        campaign.setStatus(CampaignStatus.DRAFT); // ✅ default
        return ResponseEntity.ok(campaignRepository.save(campaign));
    }

    // Get all campaigns
    @GetMapping
    public ResponseEntity<List<Campaign>> getAllCampaigns() {
        return ResponseEntity.ok(campaignRepository.findAll());
    }

    // Get campaign by ID
    @GetMapping("/{id}")
    public ResponseEntity<Campaign> getCampaignById(@PathVariable Long id) {
        return campaignRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update campaign details (only if still Draft or Scheduled)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCampaign(@PathVariable Long id, @RequestBody Campaign updatedCampaign) {
        return campaignRepository.findById(id).map(campaign -> {
            if (campaign.getStatus() == CampaignStatus.SENT ||
                campaign.getStatus() == CampaignStatus.FAILED ||
                campaign.getStatus() == CampaignStatus.CANCELED) {
                return ResponseEntity.badRequest().body("Cannot update a campaign once it's Sent, Failed, or Canceled.");
            }
            campaign.setTitle(updatedCampaign.getTitle());
            campaign.setSubject(updatedCampaign.getSubject());
            campaign.setTemplate(updatedCampaign.getTemplate());
            campaign.setCreatedBy(updatedCampaign.getCreatedBy());
            campaign.setMailingList(updatedCampaign.getMailingList());
            return ResponseEntity.ok(campaignRepository.save(campaign));
        }).orElse(ResponseEntity.notFound().build());
    }




    // Schedule campaign
    @PostMapping("/{id}/schedule")
    public ResponseEntity<Campaign> scheduleCampaign(@PathVariable Long id, @RequestBody ScheduleRequest request) {
        return campaignRepository.findById(id).map(campaign -> {
        	campaignService.scheduleCampaign(id, request.getScheduledAt());
            campaign.setScheduledAt(request.getScheduledAt());
            campaign.setStatus(CampaignStatus.SCHEDULED);
            return ResponseEntity.ok(campaignRepository.save(campaign));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Send campaign (mark as SENT)
    @PostMapping("/{id}/send")
    public ResponseEntity<Campaign> markAsSent(@PathVariable Long id) {
        return campaignRepository.findById(id).map(campaign -> {
        	campaignService.sendCampaign(campaign);
            campaign.setStatus(CampaignStatus.SENT);
            campaign.setScheduledAt(LocalDateTime.now());
            return ResponseEntity.ok(campaignRepository.save(campaign));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Mark campaign as FAILED
    @PostMapping("/{id}/fail")
    public ResponseEntity<Campaign> markAsFailed(@PathVariable Long id) {
        return campaignRepository.findById(id).map(campaign -> {
            campaign.setStatus(CampaignStatus.FAILED);
            return ResponseEntity.ok(campaignRepository.save(campaign));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Cancel campaign (if not yet sent)
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelCampaign(@PathVariable Long id) {
        return campaignRepository.findById(id).map(campaign -> {
            if (campaign.getStatus() == CampaignStatus.SENT) {
                return ResponseEntity.badRequest().body("❌ Can't cancel a campaign that is already sent");
            }
            campaign.setStatus(CampaignStatus.CANCELED);
            return ResponseEntity.ok(campaignRepository.save(campaign));
        }).orElse(ResponseEntity.notFound().build());
    }
    
    // 🔍 Find campaigns by createdBy
    @GetMapping("/created-by/{userId}")
    public ResponseEntity<List<Campaign>> getCampaignsByCreatedBy(@PathVariable Long userId) {
        List<Campaign> campaigns = campaignRepository.findByCreatedById(userId);

        if (campaigns.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(campaigns); // 200 OK with campaigns
    }

}