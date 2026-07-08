package com.example.Expense_Split.Repository;

import com.example.Expense_Split.Entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transactions, Integer> {

    List<Transactions> findByPaidById(int userId);
}
