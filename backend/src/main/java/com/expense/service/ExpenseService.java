package com.expense.service;

import com.expense.model.Expense;
import com.expense.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }
    
    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }
    
    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public List<Expense> getExpensesByCategory(Long categoryId) {
        return expenseRepository.findByCategory_Id(categoryId);
    }

    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }
    
    public Map<String, Object> getExpensesByTypeGrouped() {
        List<Object[]> results = expenseRepository.findExpensesByCategory();
        Map<String, Object> groupedExpenses = new HashMap<>();
        
        for (Object[] result : results) {
            groupedExpenses.put((String) result[0], result[1]);
        }
        
        return groupedExpenses;
    }
    
}