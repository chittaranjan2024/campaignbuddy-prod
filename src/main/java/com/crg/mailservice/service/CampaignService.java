package com.crg.mailservice.service;

import java.time.LocalDateTime;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.crg.mailservice.dto.CampaignDto;
import com.crg.mailservice.model.Campaign;
import com.crg.mailservice.model.CampaignActivity;
import com.crg.mailservice.model.CampaignStatus;
import com.crg.mailservice.model.Contact;
import com.crg.mailservice.model.MailingList;
import com.crg.mailservice.model.User;
import com.crg.mailservice.repository.CampaignActivityRepository;
import com.crg.mailservice.repository.CampaignRepository;
import com.crg.mailservice.repository.MailingListRepository;
import com.crg.mailservice.repository.TemplateRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class CampaignService {

    private final JavaMailSender mailSender;
    private final CampaignRepository campaignRepo;
    private final CampaignActivityRepository activityRepo;
    private final TemplateRepository templateRepo;
    private final MailingListRepository mailingListRepo;

    public Campaign createCampaign(CampaignDto dto, User user) {
        Campaign campaign = new Campaign();
        campaign.setTitle(dto.getTitle());
        campaign.setSubject(dto.getSubject());
        campaign.setScheduledAt(dto.getScheduledAt());
        campaign.setTemplate(templateRepo.findById(dto.getTemplateId()).orElseThrow());
        campaign.setMailingList(mailingListRepo.findById(dto.getMailingListId()).orElseThrow());
        campaign.setCreatedBy(user);

        // Default: If scheduledAt provided -> Scheduled, else Draft
        if (dto.getScheduledAt() != null && dto.getScheduledAt().isAfter(LocalDateTime.now())) {
            campaign.setStatus(CampaignStatus.SCHEDULED);
        } else {
            campaign.setStatus(CampaignStatus.DRAFT);
        }

        campaign.setCreatedAt(LocalDateTime.now());
        campaign.setUpdatedAt(LocalDateTime.now());

        return campaignRepo.save(campaign);
    }

    public void sendCampaign(Campaign campaign) {
        if (campaign.getStatus() == CampaignStatus.CANCELED) {
            return; // Cannot send cancelled campaigns
        }

        if (campaign.getMailingList() == null || campaign.getMailingList().getContacts() == null) {
            campaign.setStatus(CampaignStatus.FAILED);
            campaign.setUpdatedAt(LocalDateTime.now());
            campaignRepo.save(campaign);
            return;
        }

        boolean allFailed = true;

        for (Contact contact : campaign.getMailingList().getContacts()) {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
                helper.setTo(contact.getEmail());
                helper.setSubject(campaign.getSubject() != null ? campaign.getSubject() : campaign.getTemplate().getName());
                helper.setText(campaign.getTemplate().getContent(), true); // ✅ true for HTML

                mailSender.send(mimeMessage);

                activityRepo.save(new CampaignActivity(
                    null, campaign, contact.getEmail(), true, false, false, LocalDateTime.now()
                ));

                allFailed = false;
            } catch (MessagingException e) {
                activityRepo.save(new CampaignActivity(
                    null, campaign, contact.getEmail(), false, false, false, LocalDateTime.now()
                ));
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Schedule a campaign at a specific datetime.
     * @param campaignId The campaign to schedule.
     * @param scheduledAt The datetime to schedule the campaign.
     * @return The updated campaign.
     */
    public Campaign scheduleCampaign(Long campaignId, LocalDateTime scheduledAt) {
        Campaign campaign = campaignRepo.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));

        if (campaign.getStatus() == CampaignStatus.SENT) {
            throw new IllegalStateException("Cannot schedule a campaign that is already sent");
        }

        campaign.setScheduledAt(scheduledAt);
        campaign.setStatus(CampaignStatus.SCHEDULED);
        campaign.setUpdatedAt(LocalDateTime.now());

        return campaignRepo.save(campaign);
    }
    public Campaign cancelCampaign(Long campaignId) {
        Campaign campaign = campaignRepo.findById(campaignId).orElseThrow();
        if (campaign.getStatus() == CampaignStatus.SENT) {
            throw new IllegalStateException("Cannot cancel a campaign that is already sent.");
        }
        campaign.setStatus(CampaignStatus.CANCELED);
        campaign.setUpdatedAt(LocalDateTime.now());
        return campaignRepo.save(campaign);
    }
}
