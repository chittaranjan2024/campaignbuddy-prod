package com.crg.mailservice.service;

import com.crg.mailservice.model.Campaign;
import com.crg.mailservice.model.CampaignActivity;
import com.crg.mailservice.model.Contact;
import com.crg.mailservice.repository.CampaignActivityRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final CampaignActivityRepository activityRepository;

    // 1. Send plain text email
    public void sendTextEmail(Campaign campaign, Contact contact) {
        try {
            String subject = replacePlaceholders(campaign.getTemplate().getName(), contact);
            String body = replacePlaceholders(campaign.getTemplate().getContent(), contact);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(contact.getEmail());
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("chittaranjan@guvi.in");

            mailSender.send(message);
            saveActivity(campaign, contact, true);
            log.info("Text email sent to {}", contact.getEmail());
        } catch (Exception e) {
            log.error("Failed to send text email to {}: {}", contact.getEmail(), e.getMessage());
            saveActivity(campaign, contact, false);
        }
    }

    // 2. Send HTML multimedia email
    public void sendHtmlEmail(Campaign campaign, Contact contact) {
        try {
            String subject = replacePlaceholders(campaign.getTemplate().getName(), contact);
            String htmlBody = replacePlaceholders(campaign.getTemplate().getContent(), contact); // HTML

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);
            helper.setTo(contact.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML
            helper.setFrom("chittaranjan@guvi.in");

            mailSender.send(message);
            saveActivity(campaign, contact, true);
            log.info("HTML email sent to {}", contact.getEmail());
        } catch (Exception e) {
            log.error("Failed to send HTML email to {}: {}", contact.getEmail(), e.getMessage());
            saveActivity(campaign, contact, false);
        }
    }

    // 3. Send HTML email with file attachment
    public void sendEmailWithAttachment(Campaign campaign, Contact contact, String filePath) {
        try {
            String subject = replacePlaceholders(campaign.getTemplate().getName(), contact);
            String htmlBody = replacePlaceholders(campaign.getTemplate().getContent(), contact);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // multipart = true

            helper.setTo(contact.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            helper.setFrom("chittaranjan@guvi.in");

            FileSystemResource file = new FileSystemResource(new File(filePath));
            if (file.exists()) {
                helper.addAttachment(file.getFilename(), file);
            }

            mailSender.send(message);
            saveActivity(campaign, contact, true);
            log.info("Email with attachment sent to {}", contact.getEmail());
        } catch (Exception e) {
            log.error("Failed to send email with attachment to {}: {}", contact.getEmail(), e.getMessage());
            saveActivity(campaign, contact, false);
        }
    }

    // Utility to save delivery status
    private void saveActivity(Campaign campaign, Contact contact, boolean delivered) {
        CampaignActivity activity = new CampaignActivity();
        activity.setCampaign(campaign);
        activity.setRecipient(contact.getEmail());
        activity.setDelivered(delivered);
        activity.setOpened(false);
        activity.setClicked(false);
        activity.setTimestamp(LocalDateTime.now());
        activityRepository.save(activity);
    }

    // Utility to replace template placeholders
    private String replacePlaceholders(String content, Contact contact) {
        return content
                .replace("{{name}}", contact.getName() != null ? contact.getName() : "")
                .replace("{{email}}", contact.getEmail() != null ? contact.getEmail() : "");
    }
    
    //for testing purpose from rest client
    public void sendEmailWithAttachment(Contact contact, String subject, String body, MultipartFile file) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(contact.getEmail());
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom("chittaranjan@guvi.in");

            if (file != null && !file.isEmpty()) {
                helper.addAttachment(file.getOriginalFilename(), file);
            }

            mailSender.send(message);
            log.info("Email with attachment sent to {}", contact.getEmail());

            recordActivityManual(contact.getEmail(), subject, true);
        } catch (MessagingException e) {
            log.error("Failed to send email with attachment to {}: {}", contact.getEmail(), e.getMessage());
            recordActivityManual(contact.getEmail(), subject, false);
        }
    }

    private void recordActivityManual(String email, String subject, boolean delivered) {
        CampaignActivity activity = new CampaignActivity();
        activity.setRecipient(email);
        activity.setDelivered(delivered);
        activity.setOpened(false);
        activity.setClicked(false);
        activity.setTimestamp(LocalDateTime.now());

        activityRepository.save(activity);
    }
}
