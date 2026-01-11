package com.example.financetracker.repo;

import com.example.financetracker.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepo extends JpaRepository<Payment, Long> {
    List<Payment> findByUser_Id(Long userId); // works with @ManyToOne User
}
