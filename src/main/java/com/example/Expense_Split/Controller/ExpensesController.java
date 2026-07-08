    package com.example.Expense_Split.Controller;

    import com.example.Expense_Split.DTO.ExpenseRequestDTO;
    import com.example.Expense_Split.DTO.ExpenseResponseDTO;
    import com.example.Expense_Split.Service.ExpensesService;
    import jakarta.validation.Valid;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import java.util.List;

    @RestController
    @RequestMapping("/expenses")
    public class ExpensesController {

        private final ExpensesService expensesService;

        public ExpensesController(ExpensesService expensesService) {
            this.expensesService = expensesService;
        }


        @GetMapping("/test")
        public String test() {
            return "Expense API Working!";
        }

        //  Add Expense (Auto Split)
        @PostMapping("/add")
        public ResponseEntity<ExpenseResponseDTO> addExpense(
                @Valid @RequestBody ExpenseRequestDTO request) {

            return ResponseEntity.ok(expensesService.addExpense(request));
        }
        @GetMapping("/group/{groupId}")
        public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByGroup(@PathVariable int groupId) {
            return ResponseEntity.ok(expensesService.getExpensesByGroup(groupId));
        }
        @GetMapping("/user/{userId}")
        public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByUser(@PathVariable int userId) {
            return ResponseEntity.ok(expensesService.getExpensesByUser(userId));
        }

        @GetMapping("/high-value")
        public ResponseEntity<List<ExpenseResponseDTO>> getHighValue() {
            return ResponseEntity.ok(expensesService.getHighValue());
        }
    }
