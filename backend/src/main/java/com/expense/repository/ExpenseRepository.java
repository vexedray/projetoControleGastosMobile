package com.expense.repository;

import com.expense.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    List<Expense> findByCategory_Id(Long categoryId);
    
    @Query("SELECT c.name, SUM(e.value) FROM Expense e JOIN e.category c GROUP BY c.name")
    List<Object[]> findExpensesByCategory();
}