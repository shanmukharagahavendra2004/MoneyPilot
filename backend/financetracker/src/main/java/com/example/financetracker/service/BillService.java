package com.example.financetracker.service;

import com.example.financetracker.model.Bill;
import com.example.financetracker.repo.BillRepo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class BillService {

    private final BillRepo billRepository;

    public BillService(BillRepo billRepository) {
        this.billRepository = billRepository;
    }

    public Bill addBill(Bill bill) {
        if (bill == null) {
            throw new IllegalArgumentException("Bill cannot be null");
        }

        try {
            bill.setPaymentStatus("PAID"); // since bill is added after payment
            return billRepository.save(bill);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving bill: " + e.getMessage());
        }
    }

    public List<Bill> getBillsByUser(Long userId) {
        if (userId == null || userId <= 0) {
            System.err.println("Invalid userId: " + userId);
            return Collections.emptyList();
        }

        try {
            System.out.println("Fetching bills for user: " + userId);
            List<Bill> bills = billRepository.findByUser_Id(userId);
            return bills != null ? bills : Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
