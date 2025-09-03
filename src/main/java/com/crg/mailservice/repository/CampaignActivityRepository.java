package com.crg.mailservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crg.mailservice.model.Campaign;
import com.crg.mailservice.model.CampaignActivity;

public interface CampaignActivityRepository extends JpaRepository<CampaignActivity, Long> {
	 List<CampaignActivity> findByCampaign(Campaign campaign);
}
