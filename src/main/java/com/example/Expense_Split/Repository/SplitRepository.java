package com.example.Expense_Split.Repository;

import com.example.Expense_Split.Entity.Split;
import com.example.Expense_Split.Entity.SplitStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SplitRepository extends JpaRepository<Split, Integer> {

    List<Split> findByFromUserIdAndStatus(int userId, SplitStatus status);
    List<Split> findByToUserIdAndStatus(int userId, SplitStatus status);
    List<Split> findByFromUserIdAndToUserIdAndStatus(int fromUserId, int toUserId, SplitStatus status);

}
