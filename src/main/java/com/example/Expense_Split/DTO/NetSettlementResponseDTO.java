package com.example.Expense_Split.DTO;

public class NetSettlementResponseDTO {

    private String fromUser;
    private String toUser;
    private double grossOwed;
    private double grossOwedBack;
    private double netAmount;
    private String direction;

    public NetSettlementResponseDTO(String fromUser,
                                    String toUser,
                                    double grossOwed,
                                    double grossOwedBack,
                                    double netAmount,
                                    String direction) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.grossOwed = grossOwed;
        this.grossOwedBack = grossOwedBack;
        this.netAmount = netAmount;
        this.direction = direction;
    }

    public String getFromUser() { return fromUser; }
    public String getToUser() { return toUser; }
    public double getGrossOwed() { return grossOwed; }
    public double getGrossOwedBack() { return grossOwedBack; }
    public double getNetAmount() { return netAmount; }
    public String getDirection() { return direction; }
}
