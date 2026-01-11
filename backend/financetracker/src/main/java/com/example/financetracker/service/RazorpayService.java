package com.example.financetracker.service;


import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

    @Value("${razorpay.key}")
    private String keyId;

    @Value("${razorpay.key}")
    private String keySecret;

    private RazorpayClient client;

    public void init() throws Exception {
        client =new RazorpayClient(keyId,keySecret);
    }

    public Order createOrder(int amount, String currency) throws Exception {
        init();
        JSONObject options=new JSONObject();
        options.put("amount",amount*100);
        options.put("currency",currency);
        options.put("payment_capture",1);
        return client.orders.create(options);
    }

}
