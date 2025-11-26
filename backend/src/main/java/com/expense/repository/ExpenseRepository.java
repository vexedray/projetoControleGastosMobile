package com.expense.repository;

import com.expense.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    /**
     * Find expenses by user ID
     */
    List<Expense> findByUserId(Long userId);
    
    /**
     * Find expenses by category ID
     */
    List<Expense> findByCategoryId(Long categoryId);
    
    /**
     * Find expense by ID and user ID (for security)
     */
    Optional<Expense> findByIdAndUserId(Long id, Long userId);
}