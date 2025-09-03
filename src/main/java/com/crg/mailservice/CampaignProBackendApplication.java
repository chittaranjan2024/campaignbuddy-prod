package com.crg.mailservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CampaignProBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampaignProBackendApplication.class, args);
	}

}
