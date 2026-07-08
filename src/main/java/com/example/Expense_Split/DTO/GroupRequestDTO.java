package com.example.Expense_Split.DTO;

import jakarta.validation.constraints.NotBlank;

public class GroupRequestDTO {

    @NotBlank(message = "Group name cannot be empty")
    private String name;

    // Getter & Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
