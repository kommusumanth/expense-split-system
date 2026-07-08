package com.example.Expense_Split.Service;

import com.example.Expense_Split.DTO.GroupRequestDTO;
import com.example.Expense_Split.DTO.GroupResponseDTO;
import com.example.Expense_Split.Entity.Group;
import com.example.Expense_Split.Entity.User;
import com.example.Expense_Split.Exception.GroupNotFoundException;
import com.example.Expense_Split.Exception.UserNotFoundException;
import com.example.Expense_Split.Repository.GroupRepository;
import com.example.Expense_Split.Repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository,
                        UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }
    public long getMemberCount(int groupId) {
        return groupRepository.countMembersByGroupId(groupId);
    }
    //  Create Group
    public GroupResponseDTO createGroup(GroupRequestDTO request) {

        Group group = new Group();
        group.setName(request.getName());

        Group savedGroup = groupRepository.save(group);

        return mapToResponseDTO(savedGroup);
    }
    //  Add User To Group (Return Updated Group)
    public GroupResponseDTO addUserToGroup(int groupId, int userId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new GroupNotFoundException("Group not found with id: " + groupId));

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + userId));

        if (!group.getUsers().contains(user)) {
            group.getUsers().add(user);
            groupRepository.save(group);
        }

        return mapToResponseDTO(group);
    }
    //  Get Group
    public GroupResponseDTO getGroup(int id) {

        Group group = groupRepository.findById(id)
                .orElseThrow(() ->
                        new GroupNotFoundException("Group not found with id: " + id));

        return mapToResponseDTO(group);
    }
    // 🔁 Mapping

    public long getGroupCount() {
        return groupRepository.count();
    }

    private GroupResponseDTO mapToResponseDTO(Group group) {

        List<String> members = group.getUsers() == null
                ? List.of()
                : group.getUsers()
                .stream()
                .map(User::getName)
                .collect(Collectors.toList());

        return new GroupResponseDTO(
                group.getId(),
                group.getName(),
                members
        );
    }
}
