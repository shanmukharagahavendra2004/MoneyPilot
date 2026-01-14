package com.example.financetracker.repo;

import com.example.financetracker.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collections;
import java.util.List;

public interface PaymentRepo extends JpaRepository<Payment, Long> {

    // Original method – works with @ManyToOne User
    List<Payment> findByUser_Id(Long userId);

    // Optional safe helper to avoid null or invalid IDs
    default List<Payment> safeFindByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            return Collections.emptyList();
        }
        return findByUser_Id(userId);
    }
}
