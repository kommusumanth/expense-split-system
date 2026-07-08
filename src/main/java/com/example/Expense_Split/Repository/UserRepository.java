package com.example.Expense_Split.Repository;

import com.example.Expense_Split.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    @Query("""
        SELECT DISTINCT u FROM User u
        JOIN u.groups g
        JOIN g.expenses e
        GROUP BY u
        HAVING COUNT(DISTINCT g.id) > 3
        AND MIN(e.amount) > 1000
    """)
    List<User> findHighActivityUsers();


}