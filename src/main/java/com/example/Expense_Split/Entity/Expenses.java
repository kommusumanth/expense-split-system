package com.example.Expense_Split.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expenses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String description;

    @Positive
    @Column(nullable = false)
    private double amount;

    @ManyToOne(optional = false)
    private User paidBy;

    @ManyToOne
    private Group group;

    @JsonIgnore
    @OneToMany(mappedBy = "expenses")
    private List<Split> splits = new ArrayList<>();

    private LocalDateTime createdAt;
}

