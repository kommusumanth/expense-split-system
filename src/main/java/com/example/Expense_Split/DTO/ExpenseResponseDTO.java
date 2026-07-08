package com.example.Expense_Split.DTO;

import java.time.LocalDateTime;

public class ExpenseResponseDTO {

    private int id;
    private String description;
    private double amount;
    private String paidBy;
    private LocalDateTime createdAt;

    public ExpenseResponseDTO(int id, String description,
                              double amount, String paidBy,
                              LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.paidBy = paidBy;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
