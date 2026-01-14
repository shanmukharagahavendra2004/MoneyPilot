package com.example.financetracker.controller;

import com.example.financetracker.model.Payment;
import com.example.financetracker.model.PaymentVerifyRequest;
import com.example.financetracker.model.User;
import com.example.financetracker.repo.PaymentRepo;
import com.example.financetracker.repo.UserRepo;
import com.example.financetracker.service.PaymentService;
import com.razorpay.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PaymentRepo paymentRepo;

    // CREATE ORDER
    @PostMapping(value = "/create-order", produces = "application/json")
    public ResponseEntity<?> createOrder(@RequestParam BigDecimal amount) {
        try {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Amount must be greater than 0");
            }

            Order order = paymentService.createOrder(amount);

            Map<String, Object> response = new HashMap<>();
            response.put("id", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", order.get("currency"));
            response.put("receipt", order.get("receipt"));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating order: " + e.getMessage());
        }
    }

    // VERIFY & SAVE PAYMENT
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PaymentVerifyRequest request
    ) {
        try {
            if (userDetails == null || userDetails.getUsername() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not authenticated");
            }
            if (request == null || request.getRazorpayPaymentId() == null ||
                    request.getRazorpayOrderId() == null || request.getRazorpaySignature() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid payment request data");
            }

            // Fetch User entity safely
            User user = userRepo.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Call service
            paymentService.verifyAndSavePayment(
                    request.getRazorpayPaymentId(),
                    request.getRazorpayOrderId(),
                    request.getRazorpaySignature(),
                    user,
                    request.getAmount(),
                    request.getPaymentType(),
                    request.getBillName(),
                    request.getCategory()
            );

            return ResponseEntity.ok("Payment verified successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error verifying payment: " + e.getMessage());
        }
    }

    // GET PAYMENTS OF LOGGED-IN USER
    @GetMapping("/user")
    public ResponseEntity<?> getUserPayments(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null || userDetails.getUsername() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not authenticated");
            }

            User user = userRepo.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Payment> payments = paymentService.getPaymentsByUser(user.getId());
            return ResponseEntity.ok(payments);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching payments: " + e.getMessage());
        }
    }
}
