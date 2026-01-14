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

        try {
            // 1️⃣ Defensive: JWT must exist
            if (userDetails == null || userDetails.getUsername() == null) {
                return ResponseEntity.status(401).body("Unauthorized: JWT missing");
            }

            String username = userDetails.getUsername();

            // 2️⃣ Defensive: User must exist
            var userOpt = userRepo.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body("Unauthorized: user not found");
            }

            var user = userOpt.get();
            Long userId = user.getId();

            // 3️⃣ Defensive: Request body must exist
            if (body == null || body.isEmpty()) {
                return ResponseEntity.badRequest().body("Request body cannot be empty");
            }

            System.out.println("JWT userId = " + userId);
            System.out.println("Incoming request: " + body);

            // 4️⃣ Inject authenticated userId (never trust frontend)
            body.put("userId", userId);

            String flaskUrl = "http://127.0.0.1:5000/analyze";

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    flaskUrl,
                    body,
                    Map.class
            );

            // 5️⃣ Defensive: Flask must respond
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return ResponseEntity.status(502)
                        .body("AI service unavailable. Please try again later.");
            }

            System.out.println("Flask response: " + response.getBody());

            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            // 6️⃣ Never expose internal stacktrace
            System.err.println("AI Controller Error: " + e.getMessage());
            return ResponseEntity.status(500)
                    .body("Internal error while processing AI request");
        }
    }
}
