package com.example.financetracker.controller;

import com.example.financetracker.model.Bill;
import com.example.financetracker.model.User;
import com.example.financetracker.repo.UserRepo;
import com.example.financetracker.service.BillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "http://localhost:5173")
public class BillController {

    private final BillService billService;
    private final UserRepo userRepo;

    public BillController(BillService billService, UserRepo userRepo) {
        this.billService = billService;
        this.userRepo = userRepo;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addBill(@RequestBody Bill bill) {
        try {
            if (bill == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Bill cannot be null");
            }
            Bill savedBill = billService.addBill(bill);
            return ResponseEntity.ok(savedBill);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding bill: " + e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getBills(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null || userDetails.getUsername() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not authenticated");
            }

            String username = userDetails.getUsername();
            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Bill> bills = billService.getBillsByUser(user.getId());
            if (bills == null) {
                bills = Collections.emptyList();
            }

            System.out.println("Bills for user " + user.getId() + ": " + bills);
            return ResponseEntity.ok(bills);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching bills: " + e.getMessage());
        }
    }
}
