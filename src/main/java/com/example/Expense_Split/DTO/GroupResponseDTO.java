package com.example.Expense_Split.DTO;

import java.util.List;

public class GroupResponseDTO {

    private int id;
    private String name;
    private List<String> members;
    private int membersCount;

    public GroupResponseDTO(int id, String name, List<String> members) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.membersCount = members.size();
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getMembers() {
        return members;
    }
    public int getMembersCount() {return membersCount;}

}
