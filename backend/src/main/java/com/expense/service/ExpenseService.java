package com.expense.service;

import com.expense.model.Expense;
import com.expense.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    /**
     * Find all expenses
     */
    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }
    
    /**
     * Find expense by ID
     */
    public Optional<Expense> findById(Long id) {
        return expenseRepository.findById(id);
    }
    
    /**
     * Find expenses by user ID
     */
    public List<Expense> findByUserId(Long userId) {
        return expenseRepository.findByUserId(userId);
    }
    
    /**
     * Find expenses by category ID
     */
    public List<Expense> findByCategoryId(Long categoryId) {
        return expenseRepository.findByCategoryId(categoryId);
    }
    
    /**
     * Save expense
     */
    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }
    
    /**
     * Delete expense by ID
     */
    public void deleteById(Long id) {
        expenseRepository.deleteById(id);
    }
    
    /**
     * Check if expense exists
     */
    public boolean existsById(Long id) {
        return expenseRepository.existsById(id);
    }
}