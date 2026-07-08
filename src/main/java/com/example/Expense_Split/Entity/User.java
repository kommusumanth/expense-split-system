package com.example.Expense_Split.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friends = new ArrayList<>();


    @JsonIgnore
    @ManyToMany(mappedBy = "users")
    private List<Group> groups;

    @JsonIgnore
    @OneToMany(mappedBy = "paidBy")
    private List<Expenses> expenses;

    @JsonIgnore
    @OneToMany(mappedBy = "fromUser")
    private List<Split> fromSplits;

    @JsonIgnore
    @OneToMany(mappedBy = "toUser")
    private List<Split> toSplits;

    @JsonIgnore
    @OneToMany(mappedBy = "paidBy")
    private List<Transactions> transactions;

}
