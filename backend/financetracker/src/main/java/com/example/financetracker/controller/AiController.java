package com.example.financetracker.controller;

import com.example.financetracker.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AiController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserRepo userRepo;

    @PostMapping("/query")
    public ResponseEntity<?> queryAI(@RequestBody Map<String, Object> body,
                                     @AuthenticationPrincipal UserDetails userDetails) {

        // ✅ Get the currently logged-in username from JWT
        String username = userDetails.getUsername();

        // Fetch User entity to get the actual userId
        var user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = user.getId();

        System.out.println("JWT userId = " + userId);
        System.out.println("Incoming request: " + body);

        // Add userId to the request for Flask
        body.put("userId", userId);

        String flaskUrl = "http://127.0.0.1:5000/analyze";

        ResponseEntity<Map> response = restTemplate.postForEntity(
                flaskUrl,
                body,
                Map.class
        );

        System.out.println("Flask response: " + response.getBody());

        return ResponseEntity.ok(response.getBody());
    }

}
