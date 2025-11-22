package com.expense.controller;

import com.expense.dto.request.ExpenseRequestDTO;
import com.expense.dto.response.ExpenseResponseDTO;
import com.expense.mapper.ExpenseMapper;
import com.expense.model.Expense;
import com.expense.model.Category;
import com.expense.model.User;
import com.expense.service.ExpenseService;
import com.expense.service.CategoryService;
import com.expense.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private ExpenseMapper expenseMapper;

    @GetMapping
    public ResponseEntity<List<ExpenseResponseDTO>> getAllExpenses() {
        logger.info("GET /api/expenses - Fetching all expenses");
        List<Expense> expenses = expenseService.findAll();
        List<ExpenseResponseDTO> response = expenses.stream()
                .map(expenseMapper::toResponseDTO)
                .collect(Collectors.toList());
        logger.info("Found {} expenses", expenses.size());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> getExpenseById(@PathVariable Long id) {
        logger.info("GET /api/expenses/{} - Fetching expense by ID", id);
        return expenseService.findById(id)
                .map(expense -> {
                    logger.info("Expense found: amount={}", expense.getAmount());
                    return ResponseEntity.ok(expenseMapper.toResponseDTO(expense));
                })
                .orElseGet(() -> {
                    logger.warn("Expense with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByUser(@PathVariable Long userId) {
        logger.info("GET /api/expenses/user/{} - Fetching user expenses", userId);
        
        return userService.getUserById(userId)
                .map(user -> {
                    List<Expense> expenses = expenseService.findByUserId(userId);
                    List<ExpenseResponseDTO> response = expenses.stream()
                            .map(expenseMapper::toResponseDTO)
                            .collect(Collectors.toList());
                    logger.info("Found {} expenses for user {}", expenses.size(), userId);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    logger.warn("User with ID {} not found", userId);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByCategory(@PathVariable Long categoryId) {
        logger.info("GET /api/expenses/category/{} - Fetching category expenses", categoryId);
        
        return categoryService.findById(categoryId)
                .map(category -> {
                    List<Expense> expenses = expenseService.findByCategoryId(categoryId);
                    List<ExpenseResponseDTO> response = expenses.stream()
                            .map(expenseMapper::toResponseDTO)
                            .collect(Collectors.toList());
                    logger.info("Found {} expenses for category {}", expenses.size(), categoryId);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    logger.warn("Category with ID {} not found", categoryId);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> createExpense(@Valid @RequestBody ExpenseRequestDTO requestDTO) {
        logger.info("POST /api/expenses - Creating new expense");
        
        try {
            Category category = categoryService.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> {
                        logger.error("Category with ID {} not found", requestDTO.getCategoryId());
                        return new RuntimeException("Category not found");
                    });
                    
            User user = userService.getUserById(requestDTO.getUserId())
                    .orElseThrow(() -> {
                        logger.error("User with ID {} not found", requestDTO.getUserId());
                        return new RuntimeException("User not found");
                    });

            Expense expense = expenseMapper.toEntity(requestDTO);
            expense.setCategory(category);
            expense.setUser(user);
            
            Expense savedExpense = expenseService.save(expense);
            
            logger.info("Expense created with ID: {} - value: {}", savedExpense.getId(), requestDTO.getAmount());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(expenseMapper.toResponseDTO(savedExpense));
            
        } catch (RuntimeException e) {
            logger.error("Error creating expense: {}", e.getMessage());
            throw e;
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequestDTO requestDTO) {
        logger.info("PUT /api/expenses/{} - Updating expense", id);
        
        try {
            return expenseService.findById(id)
                    .map(existingExpense -> {
                        existingExpense.setAmount(requestDTO.getAmount());
                        existingExpense.setDescription(requestDTO.getDescription());
                        
                        if (requestDTO.getCategoryId() != null) {
                            Category category = categoryService.findById(requestDTO.getCategoryId())
                                    .orElseThrow(() -> new RuntimeException("Category not found"));
                            existingExpense.setCategory(category);
                        }
                        
                        Expense updated = expenseService.save(existingExpense);
                        logger.info("Expense {} updated successfully", id);
                        return ResponseEntity.ok(expenseMapper.toResponseDTO(updated));
                    })
                    .orElseGet(() -> {
                        logger.warn("Expense with ID {} not found for update", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error updating expense: {}", e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        logger.info("DELETE /api/expenses/{} - Deleting expense", id);
        
        return expenseService.findById(id)
                .map(expense -> {
                    expenseService.deleteById(id);
                    logger.info("Expense {} deleted successfully", id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> {
                    logger.warn("Expense with ID {} not found for deletion", id);
                    return ResponseEntity.notFound().build();
                });
    }
}