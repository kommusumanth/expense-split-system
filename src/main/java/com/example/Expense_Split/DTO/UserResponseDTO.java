package com.example.Expense_Split.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UserResponseDTO {

    private int id;
    private String name;
    private String email;

    public UserResponseDTO(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
