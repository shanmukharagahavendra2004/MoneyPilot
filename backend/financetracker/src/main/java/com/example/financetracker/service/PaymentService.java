package com.example.financetracker.service;

import com.example.financetracker.model.Bill;
import com.example.financetracker.model.Payment;
import com.example.financetracker.model.User;
import com.example.financetracker.repo.BillRepo;
import com.example.financetracker.repo.PaymentRepo;
import com.example.financetracker.repo.UserRepo;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepo paymentRepository;
    private final BillRepo billRepo;
    private final UserRepo userRepo;
    private final String razorpaySecret;

    public PaymentService(
            @Value("${razorpay.key}") String key,
            @Value("${razorpay.secret}") String secret,
            PaymentRepo paymentRepository,
            BillRepo billRepo,
            UserRepo userRepo
    ) throws Exception {
        this.razorpayClient = new RazorpayClient(key, secret);
        this.razorpaySecret = secret;
        this.paymentRepository = paymentRepository;
        this.billRepo = billRepo;
        this.userRepo = userRepo;
    }

    // CREATE ORDER
    public Order createOrder(BigDecimal amountInRupees) throws Exception {
        if (amountInRupees == null || amountInRupees.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        try {
            JSONObject request = new JSONObject();
            BigDecimal amountInPaise = amountInRupees.multiply(new BigDecimal("100"));
            request.put("amount", amountInPaise.intValue());
            request.put("currency", "INR");
            request.put("receipt", "rcpt_" + System.currentTimeMillis());
            return razorpayClient.orders.create(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating Razorpay order: " + e.getMessage());
        }
    }

    // VERIFY + SAVE PAYMENT + SAVE BILL
    @Transactional
    public Payment verifyAndSavePayment(
            String razorpayPaymentId,
            String razorpayOrderId,
            String razorpaySignature,
            User user,
            BigDecimal amount,
            String paymentType,
            String billName,
            String category
    ) throws Exception {

        // Defensive input checks
        if (user == null || razorpayPaymentId == null || razorpayOrderId == null || razorpaySignature == null) {
            throw new IllegalArgumentException("Invalid payment or user details");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }

        try {
            // 1️⃣ Verify Razorpay signature
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpaySignature);

            boolean isValid = Utils.verifyPaymentSignature(options, razorpaySecret);
            if (!isValid) {
                throw new RuntimeException("Invalid Razorpay signature");
            }

            // 2️⃣ Save Payment
            Payment payment = new Payment();
            payment.setRazorpayPaymentId(razorpayPaymentId);
            payment.setRazorpayOrderId(razorpayOrderId);
            payment.setRazorpaySignature(razorpaySignature);
            payment.setUser(user);
            payment.setAmount(amount);
            payment.setCurrency("INR");
            payment.setStatus("SUCCESS");
            payment.setPaymentType(paymentType);
            payment.setBillName(billName);
            payment.setCategory(category);
            payment.setCreatedAt(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());
            Payment savedPayment = paymentRepository.save(payment);

            // 3️⃣ Save Bill
            Bill bill = new Bill();
            bill.setBillName(billName);
            bill.setAmount(amount);
            bill.setCategory(category);
            bill.setUser(user);
            bill.setPaymentStatus("PAID");
            bill.setRazorpayPaymentId(razorpayPaymentId);
            bill.setCreatedAt(LocalDateTime.now());
            billRepo.save(bill);

            return savedPayment;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error verifying/saving payment: " + e.getMessage());
        }
    }

    // GET PAYMENTS BY USER
    public List<Payment> getPaymentsByUser(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        try {
            return paymentRepository.findByUser_Id(userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching payments: " + e.getMessage());
        }
    }
}
