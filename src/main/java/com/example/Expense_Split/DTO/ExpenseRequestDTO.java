package com.example.Expense_Split.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ExpenseRequestDTO {
    @NotNull(message = "PaidById is required")
    private int paidById;
    @NotNull(message = "GroupId is required")
    private int groupId;

    @NotBlank(message = "Description required")
    private String description;

    @Positive(message = "Amount must be greater than 0")
    private double amount;

    // Getters & Setters
    public int getPaidById() {
        return paidById;
    }

    public void setPaidById(int paidById) {
        this.paidById = paidById;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
