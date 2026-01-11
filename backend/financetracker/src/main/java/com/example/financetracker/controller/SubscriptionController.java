package com.example.financetracker.controller;

import com.razorpay.Subscription;
import com.example.financetracker.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping("/create")
    public Subscription createSubscription(@RequestParam String planId, @RequestParam int totalCount) throws Exception {
        return subscriptionService.createSubscription(planId,totalCount);
    }
}
