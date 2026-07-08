package com.example.Expense_Split.Controller;


import com.example.Expense_Split.DTO.GroupRequestDTO;
import com.example.Expense_Split.DTO.GroupResponseDTO;
import com.example.Expense_Split.Entity.Group;
import com.example.Expense_Split.Service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<GroupResponseDTO> createGroup(@Valid @RequestBody GroupRequestDTO request) {
        return ResponseEntity.ok(groupService.createGroup(request));
    }

    @PostMapping("/{groupId}/add-user/{userId}")
    public ResponseEntity<GroupResponseDTO> addUserToGroup(
            @PathVariable int groupId,
            @PathVariable int userId) {

        return ResponseEntity.ok(groupService.addUserToGroup(groupId, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponseDTO> getGroup(@PathVariable int id) {
        return ResponseEntity.ok(groupService.getGroup(id));
    }
    @GetMapping("/{id}/member-count")
    public ResponseEntity<Long> getMemberCount(@PathVariable int id) {
        return ResponseEntity.ok(groupService.getMemberCount(id));
    }
    @GetMapping("/count")
    public ResponseEntity<Long> getGroupCount() {
        return ResponseEntity.ok(groupService.getGroupCount());
    }
}
