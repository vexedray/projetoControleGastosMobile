package com.expense.repository;

import com.expense.model.Expense;
import com.expense.model.Category;
import com.expense.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    /**
     * Busca despesas por ID do usuário
     */
    List<Expense> findByUser_Id(Long userId);
    
    /**
     * Busca despesas por ID da categoria
     */
    List<Expense> findByCategory_Id(Long categoryId);
    
    /**
     * Busca despesas em um período
     */
    List<Expense> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Busca despesas de um usuário (ID) em um período
     */
    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :startDate AND :endDate ORDER BY e.date DESC")
    List<Expense> findByUserIdAndDateBetween(
        @Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Agrupa despesas por categoria (nome e total) - usado para gráficos
     */
    @Query("SELECT c.name, SUM(e.value) FROM Expense e JOIN e.category c GROUP BY c.name ORDER BY SUM(e.value) DESC")
    List<Object[]> findExpensesByCategory();
    
    /**
     * Agrupa despesas por categoria para um usuário específico
     */
    @Query("SELECT c.name, SUM(e.value) FROM Expense e JOIN e.category c WHERE e.user.id = :userId GROUP BY c.name ORDER BY SUM(e.value) DESC")
    List<Object[]> findExpensesByCategoryAndUser(@Param("userId") Long userId);
    
    /**
     * Conta despesas por categoria
     */
    long countByCategory(Category category);
    
    /**
     * Conta despesas por usuário
     */
    long countByUser(User user);
}