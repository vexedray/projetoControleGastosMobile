package com.expense.repository;

import com.expense.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}