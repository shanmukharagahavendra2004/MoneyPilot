package com.example.financetracker.repo;

import com.example.financetracker.model.Bill;
import com.example.financetracker.model.CategoryAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public interface BillRepo extends JpaRepository<Bill, Long> {

    @Query("""
        SELECT new com.example.financetracker.model.CategoryAnalytics(
            b.category,
            SUM(b.amount)
        )
        FROM Bill b
        WHERE b.user.id = :userId
        AND b.createdAt BETWEEN :startDate AND :endDate
        GROUP BY b.category
    """)
    List<CategoryAnalytics> getMonthlyCategoryAnalytics(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Original method – works with @ManyToOne User
    List<Bill> findByUser_Id(Long userId);

    // ✅ Safe helper method to avoid null or invalid userId
    default List<Bill> safeFindByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            return Collections.emptyList();
        }
        List<Bill> bills = findByUser_Id(userId);
        return bills != null ? bills : Collections.emptyList();
    }
}
