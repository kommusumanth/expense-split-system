package com.example.Expense_Split.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class TransactionResponseDTO {

    private double amount;
    private String paidTo;
    private LocalDateTime timestamp;
}
