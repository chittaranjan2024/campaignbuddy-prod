package com.crg.mailservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crg.mailservice.model.CampaignActivity;
import com.crg.mailservice.repository.CampaignActivityRepository;
import com.crg.mailservice.repository.CampaignRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/campaign-activities")
@RequiredArgsConstructor
public class CampaignActivityController {

    private final CampaignActivityRepository activityRepository;
    private final CampaignRepository campaignRepository;

    // Create new campaign activity
    @PostMapping
    public ResponseEntity<CampaignActivity> createActivity(@RequestBody CampaignActivity activity) {
        activity.setTimestamp(LocalDateTime.now());
        return ResponseEntity.ok(activityRepository.save(activity));
    }

    // Get all activities
    @GetMapping
    public ResponseEntity<List<CampaignActivity>> getAllActivities() {
        return ResponseEntity.ok(activityRepository.findAll());
    }

    // Get activity by ID
    @GetMapping("/{id}")
    public ResponseEntity<CampaignActivity> getActivityById(@PathVariable Long id) {
        return activityRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get activities by campaign
    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<List<CampaignActivity>> getActivitiesByCampaign(@PathVariable Long campaignId) {
        return campaignRepository.findById(campaignId)
                .map(campaign -> ResponseEntity.ok(activityRepository.findByCampaign(campaign)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Update activity (e.g. mark as opened or clicked)
    @PutMapping("/{id}")
    public ResponseEntity<CampaignActivity> updateActivity(@PathVariable Long id, @RequestBody CampaignActivity updated) {
        return activityRepository.findById(id).map(activity -> {
            activity.setDelivered(updated.isDelivered());
            activity.setOpened(updated.isOpened());
            activity.setClicked(updated.isClicked());
            activity.setTimestamp(LocalDateTime.now());
            return ResponseEntity.ok(activityRepository.save(activity));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Delete activity
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        if (!activityRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        activityRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

