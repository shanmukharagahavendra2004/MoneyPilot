package com.example.financetracker.service;

import com.example.financetracker.model.Bill;
import com.example.financetracker.repo.BillRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillService {

    private final BillRepo billRepository;

    public BillService(BillRepo billRepository) {
        this.billRepository = billRepository;
    }

    public Bill addBill(Bill bill) {
        bill.setPaymentStatus("PAID"); // since bill is added after payment
        return billRepository.save(bill);
    }

    public List<Bill> getBillsByUser(Long userId) {
        System.out.println("Bill service");

        return billRepository.findByUser_Id(userId);
// use this when using User relation
    }
}
