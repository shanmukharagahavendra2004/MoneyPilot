package com.example.financetracker.service;

import com.example.financetracker.model.CategoryAnalytics;
import com.example.financetracker.repo.BillRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class AnalyticsService {

    private final BillRepo billRepo;

    public AnalyticsService(BillRepo billRepo) {
        this.billRepo = billRepo;
    }

    public List<CategoryAnalytics> getMonthlyCategoryAnalytics(
            Long userId,
            int month,
            int year
    ) {
        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        List<CategoryAnalytics> analytics = billRepo.getMonthlyCategoryAnalytics(userId, startDate, endDate);

        System.out.println("Monthly Analytics for user " + userId + ": " + analytics); // ✅ Logs to console

        return analytics;
    }
}
