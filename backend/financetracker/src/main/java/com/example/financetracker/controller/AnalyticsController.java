package com.example.financetracker.controller;

import com.example.financetracker.model.CategoryAnalytics;
import com.example.financetracker.service.AnalyticsService;
import com.example.financetracker.service.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final JwtService jwtService;

    public AnalyticsController(AnalyticsService analyticsService, JwtService jwtService) {
        this.analyticsService = analyticsService;
        this.jwtService = jwtService;
    }

    @GetMapping("/monthly-category")
    public List<CategoryAnalytics> getMonthlyCategoryAnalytics(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam int month,
            @RequestParam int year
    ) {
        System.out.println("Hello analytics");

        // Extract JWT token from header
        String token = authHeader.replace("Bearer ", "");

        // Get userId from JWT
        Long userId = jwtService.extractUserId(token);

        List<CategoryAnalytics> analytics = analyticsService.getMonthlyCategoryAnalytics(userId, month, year);

        System.out.println("Monthly Analytics for user " + userId + ": " + analytics); // ✅ Logs

        return analytics;
    }
}
