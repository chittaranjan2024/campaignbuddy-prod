package com.crg.mailservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.crg.mailservice.model.Campaign;
import com.crg.mailservice.model.CampaignStatus;
import com.crg.mailservice.repository.CampaignRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignScheduler {

    private final CampaignRepository campaignRepo;
    private final CampaignService campaignService;

    // Runs every minute
    @Scheduled(fixedRate = 60000) // 60,000 ms = 1 min
    public void sendScheduledCampaigns() {
        List<Campaign> campaignsToSend = campaignRepo.findByStatusAndScheduledAtBefore(
            CampaignStatus.SCHEDULED, LocalDateTime.now()
        );

        for (Campaign campaign : campaignsToSend) {
            campaignService.sendCampaign(campaign);
            campaign.setStatus(CampaignStatus.SENT);
            campaignRepo.save(campaign);
        }
    }
}
