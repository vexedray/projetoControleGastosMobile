package com.expense.service;

import com.expense.dto.request.ExpenseRequestDTO;
import com.expense.model.Category;
import com.expense.model.Expense;
import com.expense.model.User;
import com.expense.repository.CategoryRepository;
import com.expense.repository.ExpenseRepository;
import com.expense.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpenseService.class);
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    /**
     * Find all expenses for a specific user
     */
    public List<Expense> getAllExpenses(Long userId) {
        logger.info("Buscando despesas do usuário: {}", userId);
        return expenseRepository.findByUserId(userId);
    }
    
    /**
     * Find expense by ID (only if belongs to user)
     */
    public Optional<Expense> getExpenseById(Long id, Long userId) {
        logger.info("Buscando despesa {} do usuário {}", id, userId);
        return expenseRepository.findByIdAndUserId(id, userId);
    }
    
    /**
     * Create new expense for a user
     */
    public Expense createExpense(ExpenseRequestDTO expenseDTO, Long userId) {
        logger.info("Criando despesa para usuário: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Category category = categoryRepository.findByIdAndUserId(expenseDTO.getCategoryId(), userId)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada ou não pertence ao usuário"));
        
        Expense expense = new Expense();
        expense.setDescription(expenseDTO.getDescription());
        expense.setAmount(expenseDTO.getAmount());
        expense.setDate(expenseDTO.getDate().atStartOfDay());
        expense.setUser(user);
        expense.setCategory(category);
        
        return expenseRepository.save(expense);
    }
    
    /**
     * Update expense (only if belongs to user)
     */
    public Expense updateExpense(Long id, ExpenseRequestDTO expenseDTO, Long userId) {
        logger.info("Atualizando despesa {} do usuário {}", id, userId);
        
        Expense expense = expenseRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new RuntimeException("Despesa não encontrada ou não pertence ao usuário"));
        
        Category category = categoryRepository.findByIdAndUserId(expenseDTO.getCategoryId(), userId)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada ou não pertence ao usuário"));
        
        expense.setDescription(expenseDTO.getDescription());
        expense.setAmount(expenseDTO.getAmount());
        expense.setDate(expenseDTO.getDate().atStartOfDay());
        expense.setCategory(category);
        
        return expenseRepository.save(expense);
    }
    
    /**
     * Delete expense (only if belongs to user)
     */
    public void deleteExpense(Long id, Long userId) {
        logger.info("Deletando despesa {} do usuário {}", id, userId);
        
        Expense expense = expenseRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new RuntimeException("Despesa não encontrada ou não pertence ao usuário"));
        
        expenseRepository.delete(expense);
    }
    
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