package com.example.Expense_Split.Controller;


import com.example.Expense_Split.DTO.BalanceResponseDTO;
import com.example.Expense_Split.DTO.NetSettlementResponseDTO;
import com.example.Expense_Split.DTO.UserRequestDTO;
import com.example.Expense_Split.DTO.UserResponseDTO;
import com.example.Expense_Split.Entity.User;
import com.example.Expense_Split.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

        @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid   @RequestBody UserRequestDTO request) {

        return ResponseEntity.ok(userService.createUser(request));
    }

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUser(@PathVariable int id) {
        return userService.getUser(id);
    }


    // Add Friend
    @PostMapping("/{userId}/add-friend/{friendId}")
    public String addFriend(@PathVariable int userId,
                            @PathVariable int friendId) {
        return userService.addFriend(userId, friendId);
    }

    // Delete User
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id) {
        return userService.deleteUser(id);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BalanceResponseDTO> getBalance(@PathVariable int id) {
        return ResponseEntity.ok(userService.getBalance(id));
    }

    @GetMapping("/net-settlement")
    public ResponseEntity<NetSettlementResponseDTO> getNetSettlement(
            @RequestParam int userAId,
            @RequestParam int userBId) {
        return ResponseEntity.ok(userService.getNetSettlement(userAId, userBId));
    }
    @GetMapping("/high-activity")
    public ResponseEntity<List<UserResponseDTO>> getHighActivityUsers() {
        return ResponseEntity.ok(userService.getHighActivityUsers());
    }
}
