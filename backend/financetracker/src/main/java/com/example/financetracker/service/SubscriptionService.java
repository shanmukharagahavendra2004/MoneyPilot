package com.example.financetracker.service;


import com.razorpay.RazorpayClient;
//import com.razorpay.Subscription;
import org.json.JSONObject;
import com.razorpay.Subscription;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    private RazorpayClient razorpayClient;

    public SubscriptionService(
            @Value("${razorpay.key}") String key,
            @Value("${razorpay.secret}") String secret) throws Exception {
        this.razorpayClient=new RazorpayClient(key,secret);
    }

    public Subscription createSubscription(String planId, int totalCount) throws Exception {

        JSONObject request=new JSONObject();
        request.put("plan_id",planId);
        request.put("total_count",totalCount);
        request.put("quantity",1);
        request.put("customer_notify",1);

        return razorpayClient.subscriptions.create(request);


    }
}
