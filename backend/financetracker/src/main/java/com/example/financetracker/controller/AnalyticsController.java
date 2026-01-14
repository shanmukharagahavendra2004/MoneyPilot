package com.example.financetracker.controller;

import com.example.financetracker.model.CategoryAnalytics;
import com.example.financetracker.service.AnalyticsService;
import com.example.financetracker.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
    public ResponseEntity<?> getMonthlyCategoryAnalytics(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam int month,
            @RequestParam int year
    ) {
        try {
            // 1️⃣ Validate Authorization header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Missing or invalid Authorization header");
            }

            // 2️⃣ Extract JWT token and userId
            String token = authHeader.replace("Bearer ", "").trim();
            Long userId = jwtService.extractUserId(token);
            if (userId == null || userId <= 0) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid JWT token or user not found");
            }

            // 3️⃣ Validate month/year
            if (month < 1 || month > 12) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid month. Must be between 1 and 12");
            }
            if (year < 1900 || year > 3000) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid year");
            }

            // 4️⃣ Fetch analytics
            List<CategoryAnalytics> analytics = analyticsService.getMonthlyCategoryAnalytics(userId, month, year);

            System.out.println("Monthly Analytics for user " + userId + ": " + analytics);

            return ResponseEntity.ok(analytics);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching analytics: " + e.getMessage());
        }
    }
}
