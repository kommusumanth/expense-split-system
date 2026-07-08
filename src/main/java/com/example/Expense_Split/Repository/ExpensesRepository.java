package com.example.Expense_Split.Repository;
import java.util.List;
import com.example.Expense_Split.Entity.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface ExpensesRepository extends JpaRepository<Expenses, Integer> {
    List<Expenses> findByGroupId(int groupId);

    List<Expenses> findByPaidById(int userId);
    @Query ("SELECT  e FROM Expenses e WHERE e.amount >500 ORDER BY e.amount DESC")
    List<Expenses> findHighValue();
}
