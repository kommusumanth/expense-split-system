package com.example.Expense_Split.Service;


import com.example.Expense_Split.DTO.SettlementRequestDTO;
import com.example.Expense_Split.DTO.TransactionResponseDTO;
import com.example.Expense_Split.Entity.Split;
import com.example.Expense_Split.Entity.SplitStatus;
import com.example.Expense_Split.Entity.Transactions;
import com.example.Expense_Split.Exception.InvalidOperationException;
import com.example.Expense_Split.Exception.SplitNotFoundException;
import com.example.Expense_Split.Repository.SplitRepository;
import com.example.Expense_Split.Repository.TransactionRepository;
import com.example.Expense_Split.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final SplitRepository splitRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(SplitRepository splitRepository,
                              TransactionRepository transactionRepository,
                              UserRepository userRepository) {
        this.splitRepository = splitRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public String settlePayment(SettlementRequestDTO request) {

        Split split = splitRepository.findById(request.getSplitId())
                .orElseThrow(() ->
                        new SplitNotFoundException(
                                "Split not found with id: " + request.getSplitId()
                        )
                );

        if (split.getStatus() == SplitStatus.PAID) {
            throw new InvalidOperationException("Split is already settled");
        }

        // Validate correct debtor
        if (split.getFromUser().getId() != request.getPaidByUserId()) {
            throw new InvalidOperationException("Only debtor can settle this split");
        }

        // Optional safety check
        if (split.getAmount() != split.getAmount()) {
            throw new RuntimeException("Settlement amount mismatch");
        }

        // 🔹 Mark split as PAID
        split.setStatus(SplitStatus.PAID);
        splitRepository.save(split);

        // 🔹 Create transaction record
        Transactions transaction = new Transactions();
        transaction.setAmount(split.getAmount());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setPaidBy(split.getFromUser());
        transaction.setSplit(split);

        transactionRepository.save(transaction);

        return "Payment settled successfully";
    }

    public List<TransactionResponseDTO> getUserTransactions(int userId) {

        List<Transactions> transactions =
                transactionRepository.findByPaidById(userId);

        return transactions.stream()
                .map(t -> new TransactionResponseDTO(
                        t.getAmount(),
                        t.getSplit().getToUser().getName(),
                        t.getTimestamp()
                ))
                .toList();
    }

}
