package com.crg.mailservice;


import com.crg.mailservice.controller.CampaignController;
import com.crg.mailservice.dto.ScheduleRequest;
import com.crg.mailservice.model.Campaign;
import com.crg.mailservice.repository.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CampaignControllerTest {

    @InjectMocks
    private CampaignController campaignController;

    @Mock
    private CampaignRepository campaignRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCampaigns() {
        List<Campaign> campaigns = List.of(new Campaign(), new Campaign());
        when(campaignRepository.findAll()).thenReturn(campaigns);

        ResponseEntity<List<Campaign>> response = campaignController.getAllCampaigns();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(campaignRepository, times(1)).findAll();
    }

    @Test
    void testCreateCampaign() {
        Campaign campaign = new Campaign();
        campaign.setTitle("Test Campaign");

        when(campaignRepository.save(any())).thenReturn(campaign);

        ResponseEntity<Campaign> response = campaignController.createCampaign(campaign);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Test Campaign", response.getBody().getTitle());
        verify(campaignRepository, times(1)).save(any(Campaign.class));
    }

    @Test
    void testScheduleCampaign() {
        Long campaignId = 1L;
        LocalDateTime scheduleTime = LocalDateTime.now().plusDays(1);
        Campaign campaign = new Campaign();
        campaign.setId(campaignId);
        campaign.setScheduledAt(scheduleTime);

        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any())).thenReturn(campaign);

        // ✅ Use ScheduleRequest from DTO package
        ResponseEntity<Campaign> response = campaignController.scheduleCampaign(
                campaignId,
                new ScheduleRequest(scheduleTime)
        );

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(scheduleTime, response.getBody().getScheduledAt());
        verify(campaignRepository, times(1)).findById(campaignId);
        verify(campaignRepository, times(1)).save(any());
    }

}
