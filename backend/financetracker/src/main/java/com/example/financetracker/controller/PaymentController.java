package com.example.financetracker.controller;

import com.example.financetracker.model.Payment;

import com.example.financetracker.model.PaymentVerifyRequest;
import com.example.financetracker.model.User;
import com.example.financetracker.repo.PaymentRepo;
import com.example.financetracker.repo.UserRepo;
import com.example.financetracker.service.PaymentService;
import com.razorpay.Order;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Map<String, Object>> createOrder(@RequestParam BigDecimal amount) throws Exception {
        Order order = paymentService.createOrder(amount);

        Map<String, Object> response = new HashMap<>();
        response.put("id", order.get("id"));
        response.put("amount", order.get("amount"));
        response.put("currency", order.get("currency"));
        response.put("receipt", order.get("receipt"));

        return ResponseEntity.ok(response);
    }

    // VERIFY & SAVE PAYMENT
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PaymentVerifyRequest request
    ) throws Exception {
        // Fetch User entity from JWT
        User user = userRepo.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Call service with User object, not userId
        paymentService.verifyAndSavePayment(
                request.getRazorpayPaymentId(),
                request.getRazorpayOrderId(),
                request.getRazorpaySignature(),
                user,                  // ✅ pass user directly
                request.getAmount(),
                request.getPaymentType(),
                request.getBillName(),
                request.getCategory()
        );

        return ResponseEntity.ok("Payment verified successfully");
    }


    // GET PAYMENTS OF LOGGED-IN USER
    @GetMapping("/user")
    public List<Payment> getUserPayments(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return paymentService.getPaymentsByUser(user.getId());
    }
}
