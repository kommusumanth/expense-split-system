package com.example.Expense_Split.DTO;

public class SplitResponseDTO {

    private String fromUser;
    private String toUser;
    private double amount;
    private String status;

    public SplitResponseDTO(String fromUser, String toUser,
                            double amount, String status) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
        this.status = status;
    }

    // Getters
    public String getFromUser() {
        return fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}
