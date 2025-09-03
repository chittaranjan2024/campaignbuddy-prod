package com.crg.mailservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {
    @GetMapping("/api/dashboard")
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok("Welcome to your dashboard!");
    }
}
