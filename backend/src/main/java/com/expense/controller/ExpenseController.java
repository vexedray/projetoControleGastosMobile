package com.expense.controller;

import com.expense.model.Expense;
import com.expense.model.Category;
import com.expense.model.User;
import com.expense.service.ExpenseService;
import com.expense.service.CategoryService;
import com.expense.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gastos")
@CrossOrigin(origins = "*")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseService.findAll();
    }
    
    @PostMapping
    public Expense createExpense(@RequestBody Map<String, Object> expenseData) {
    BigDecimal valor = new BigDecimal(expenseData.get("valor").toString());
    Long categoryId = Long.valueOf(expenseData.get("categoryId").toString());
    Long userId = Long.valueOf(expenseData.get("userId").toString());

    Category category = categoryService.findById(categoryId)
        .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    User user = userService.findById(userId)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    Expense expense = new Expense(valor, category, user);
    return expenseService.save(expense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}