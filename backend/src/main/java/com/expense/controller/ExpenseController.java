package com.expense.controller;

import com.expense.model.Expense;
import com.expense.model.Category;
import com.expense.service.ExpenseService;
import com.expense.service.CategoryService;
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

    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseService.findAll();
    }
    
    @PostMapping
    public Expense createExpense(@RequestBody Map<String, Object> expenseData) {
        BigDecimal amount = new BigDecimal(expenseData.get("amount").toString());
        Long categoryId = Long.valueOf(expenseData.get("categoryId").toString());
        
        Category category = categoryService.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        
        Expense expense = new Expense(amount, category);
        return expenseService.save(expense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}