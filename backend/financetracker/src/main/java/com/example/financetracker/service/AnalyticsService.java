package com.example.financetracker.service;

import com.example.financetracker.model.CategoryAnalytics;
import com.example.financetracker.repo.BillRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
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
        try {
            // Defensive checks
            if (userId == null || userId <= 0) {
                System.err.println("Invalid userId: " + userId);
                return Collections.emptyList();
            }
            if (month < 1 || month > 12) {
                System.err.println("Invalid month: " + month);
                return Collections.emptyList();
            }
            if (year < 1900 || year > 3000) {
                System.err.println("Invalid year: " + year);
                return Collections.emptyList();
            }

            // Compute start and end of month
            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);

            // Fetch analytics
            List<CategoryAnalytics> analytics = billRepo.getMonthlyCategoryAnalytics(userId, startDate, endDate);
            if (analytics == null) {
                return Collections.emptyList();
            }

            System.out.println("Monthly Analytics for user " + userId + ": " + analytics);

            return analytics;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
