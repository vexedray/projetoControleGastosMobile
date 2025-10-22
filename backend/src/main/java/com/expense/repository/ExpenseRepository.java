package com.expense.repository;

import com.expense.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    List<Expense> findByCategory_Id(Long categoryId);
    
    @Query("SELECT c.nome, SUM(e.valor) FROM Expense e JOIN e.category c GROUP BY c.nome")
    List<Object[]> findExpensesByCategory();
}