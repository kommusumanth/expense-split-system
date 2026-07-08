package com.example.Expense_Split.Service;

import com.example.Expense_Split.DTO.ExpenseRequestDTO;
import com.example.Expense_Split.DTO.ExpenseResponseDTO;
import com.example.Expense_Split.Entity.*;
import com.example.Expense_Split.Exception.GroupNotFoundException;
import com.example.Expense_Split.Exception.InvalidOperationException;
import com.example.Expense_Split.Exception.UserNotFoundException;
import com.example.Expense_Split.Repository.ExpensesRepository;
import com.example.Expense_Split.Repository.GroupRepository;
import com.example.Expense_Split.Repository.SplitRepository;
import com.example.Expense_Split.Repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpensesService {

    private final ExpensesRepository expensesRepository;
    private final SplitRepository splitRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public ExpensesService(ExpensesRepository expensesRepository,
                           SplitRepository splitRepository,
                           UserRepository userRepository,
                           GroupRepository groupRepository) {

        this.expensesRepository = expensesRepository;
        this.splitRepository = splitRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;

    }

    //  Add Expense (Auto Equal Split)
    public ExpenseResponseDTO addExpense( ExpenseRequestDTO request) {

        if (request.getAmount() <= 0) {
            throw new InvalidOperationException("Amount must be greater than zero");
        }

        User paidBy = userRepository.findById(request.getPaidById())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + request.getPaidById())
                );

        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() ->
                        new GroupNotFoundException("Group not found with id: " +request.getGroupId())
                );

        if (!group.getUsers().contains(paidBy)) {
            throw new InvalidOperationException("User is not part of this group");
        }

        // Create Expense
        Expenses expense = new Expenses();
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setPaidBy(paidBy);
        expense.setGroup(group);
        expense.setCreatedAt(LocalDateTime.now());

        Expenses savedExpense = expensesRepository.save(expense);

        // Equal Split Logic
        int totalMembers = group.getUsers().size();
        double splitAmount = request.getAmount() / totalMembers;

        for (User member : group.getUsers()) {

            if (member.getId() != paidBy.getId()) {

                Split split = new Split();
                split.setFromUser(member);
                split.setToUser(paidBy);
                split.setExpenses(savedExpense);
                split.setAmount(splitAmount);
                split.setStatus(SplitStatus.UNPAID);
                split.setCreatedAt(LocalDateTime.now());

                splitRepository.save(split);
            }
        }

        return new ExpenseResponseDTO(
                savedExpense.getId(),
                savedExpense.getDescription(),
                savedExpense.getAmount(),
                paidBy.getName(),
                savedExpense.getCreatedAt()
        );
    }
    public List<ExpenseResponseDTO> getExpensesByGroup(int groupId) {
        List<Expenses> expenses = expensesRepository.findByGroupId(groupId);
        return expenses.stream()
                .map(e -> new ExpenseResponseDTO(
                        e.getId(),
                        e.getDescription(),
                        e.getAmount(),
                        e.getPaidBy().getName(),
                        e.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
    public List<ExpenseResponseDTO> getExpensesByUser(int userId) {
        List<Expenses> expenses = expensesRepository.findByPaidById(userId);
        return expenses.stream()
                .map(e -> new ExpenseResponseDTO(
                        e.getId(),
                        e.getDescription(),
                        e.getAmount(),
                        e.getPaidBy().getName(),
                        e.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
    public List<ExpenseResponseDTO> getHighValue() {
        return expensesRepository.findHighValue()
                .stream()
                .map(e -> new ExpenseResponseDTO(
                        e.getId(),
                        e.getDescription(),
                        e.getAmount(),
                        e.getPaidBy().getName(),
                        e.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

}