package com.example.Expense_Split.DTO;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SettlementRequestDTO {

    @NotNull(message = "SplitId is required")
    private Integer splitId;

    @NotNull(message = "PaidByUserId is required")
    private Integer paidByUserId;

}
