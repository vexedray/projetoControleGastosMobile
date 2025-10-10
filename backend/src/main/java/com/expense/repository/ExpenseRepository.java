package com.expense.repository;

import com.expense.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    List<Expense> findByTipo(String tipo);
    
    @Query("SELECT e.tipo, SUM(e.valor) FROM Expense e GROUP BY e.tipo")
    List<Object[]> findExpensesByType();
}