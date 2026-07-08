package com.example.Expense_Split.DTO;
import java.util.List;

public class UserGroupdetailDTO {
    private int usrId;
    private String userName;
    private String email;
    private List<GroupResponseDTO>groups;
    public UserGroupdetailDTO(int usrId, String userName, String email) {
        this.usrId = usrId;
        this.userName = userName;
        this.email = email;
        this.groups=groups;
    }
    public int getUsrId() {
        return usrId;
    }
    public String getUserName() {
        return userName;
    }
    public String getEmail() {
        return email;
    }
    public List<GroupResponseDTO>getGroups(){
        return groups;
    }


}
