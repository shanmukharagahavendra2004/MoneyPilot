package com.example.financetracker.model;

import java.math.BigDecimal;

public class CategoryAnalytics {
    private String category;
    private BigDecimal totalAmount;

    public CategoryAnalytics(String category, BigDecimal totalAmount) {
        this.category = category;
        this.totalAmount = totalAmount;
    }

    // getters & setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    // ✅ Add this
    @Override
    public String toString() {
        return "CategoryAnalytics{" +
                "category='" + category + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
