package com.example.Expense_Split.Controller;


import com.example.Expense_Split.DTO.SettlementRequestDTO;
import com.example.Expense_Split.DTO.TransactionResponseDTO;
import com.example.Expense_Split.Service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/settle")
    public ResponseEntity<String> settle(@Valid @RequestBody SettlementRequestDTO request) {
        String response = transactionService.settlePayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public List<TransactionResponseDTO> getUserTransactions(
            @PathVariable int userId) {

        return transactionService.getUserTransactions(userId);
    }
}
