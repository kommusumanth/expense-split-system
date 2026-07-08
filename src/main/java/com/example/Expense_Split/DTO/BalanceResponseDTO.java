package com.example.Expense_Split.DTO;

import java.util.Map;

public class BalanceResponseDTO {

    private double totalYouOwe;
    private double totalYouAreOwed;
    private double netBalance;
    private Map<String, Double> breakdown;

    public BalanceResponseDTO(double totalYouOwe,
                              double totalYouAreOwed,
                              double netBalance,
                              Map<String, Double> breakdown) {
        this.totalYouOwe = totalYouOwe;
        this.totalYouAreOwed = totalYouAreOwed;
        this.netBalance = netBalance;
        this.breakdown = breakdown;
    }

    public double getTotalYouOwe() {
        return totalYouOwe;
    }

    public double getTotalYouAreOwed() {
        return totalYouAreOwed;
    }

    public double getNetBalance() {
        return netBalance;
    }

    public Map<String, Double> getBreakdown() {
        return breakdown;
    }
}
