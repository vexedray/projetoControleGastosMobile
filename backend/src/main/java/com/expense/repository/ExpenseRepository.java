package com.expense.repository;

import com.expense.model.Expense;
import com.expense.model.Category;
import com.expense.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    // ==================== BUSCAS POR RELACIONAMENTO ====================
    
    /**
     * Busca despesas por usuário
     */
    List<Expense> findByUser(User user);
    
    /**
     * Busca despesas por categoria
     */
    List<Expense> findByCategory(Category category);
    
    /**
     * Busca despesas por ID do usuário
     */
    List<Expense> findByUser_Id(Long userId);
    
    /**
     * Busca despesas por ID da categoria
     */
    List<Expense> findByCategory_Id(Long categoryId);
    
    // ==================== BUSCAS COM ORDENAÇÃO ====================
    
    /**
     * Busca despesas por usuário ordenadas por data (mais recente primeiro)
     */
    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId ORDER BY e.date DESC")
    List<Expense> findByUserIdOrderByDateDesc(@Param("userId") Long userId);
    
    /**
     * Busca despesas por categoria ordenadas por data
     */
    @Query("SELECT e FROM Expense e WHERE e.category.id = :categoryId ORDER BY e.date DESC")
    List<Expense> findByCategoryIdOrderByDateDesc(@Param("categoryId") Long categoryId);
    
    /**
     * Busca todas despesas ordenadas por data
     */
    List<Expense> findAllByOrderByDateDesc();
    
    /**
     * Busca todas despesas ordenadas por valor
     */
    List<Expense> findAllByOrderByValueDesc();
    
    // ==================== BUSCAS POR DATA ====================
    
    /**
     * Busca despesas em um período
     */
    List<Expense> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Busca despesas de um usuário em um período
     */
    List<Expense> findByUserAndDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
    
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
     * Busca despesas de uma categoria em um período
     */
    @Query("SELECT e FROM Expense e WHERE e.category.id = :categoryId AND e.date BETWEEN :startDate AND :endDate ORDER BY e.date DESC")
    List<Expense> findByCategoryIdAndDateBetween(
        @Param("categoryId") Long categoryId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Busca despesas após uma data
     */
    List<Expense> findByDateAfter(LocalDateTime date);
    
    /**
     * Busca despesas antes de uma data
     */
    List<Expense> findByDateBefore(LocalDateTime date);
    
    // ==================== BUSCAS POR VALOR ====================
    
    /**
     * Busca despesas com valor maior que
     */
    List<Expense> findByValueGreaterThan(BigDecimal value);
    
    /**
     * Busca despesas com valor menor que
     */
    List<Expense> findByValueLessThan(BigDecimal value);
    
    /**
     * Busca despesas com valor entre dois valores
     */
    List<Expense> findByValueBetween(BigDecimal minValue, BigDecimal maxValue);
    
    // ==================== QUERIES DE AGREGAÇÃO ====================
    
    /**
     * Agrupa despesas por categoria (nome e total)
     */
    @Query("SELECT c.name, SUM(e.value) FROM Expense e JOIN e.category c GROUP BY c.name ORDER BY SUM(e.value) DESC")
    List<Object[]> findExpensesByCategory();
    
    /**
     * Agrupa despesas por categoria para um usuário específico
     */
    @Query("SELECT c.name, SUM(e.value) FROM Expense e JOIN e.category c WHERE e.user.id = :userId GROUP BY c.name ORDER BY SUM(e.value) DESC")
    List<Object[]> findExpensesByCategoryAndUser(@Param("userId") Long userId);
    
    /**
     * Agrupa despesas por usuário
     */
    @Query("SELECT u.name, SUM(e.value) FROM Expense e JOIN e.user u GROUP BY u.name ORDER BY SUM(e.value) DESC")
    List<Object[]> findExpensesByUser();
    
    /**
     * Soma total de despesas
     */
    @Query("SELECT SUM(e.value) FROM Expense e")
    BigDecimal getTotalExpenses();
    
    /**
     * Soma total de despesas de um usuário
     */
    @Query("SELECT SUM(e.value) FROM Expense e WHERE e.user.id = :userId")
    BigDecimal getTotalByUserId(@Param("userId") Long userId);
    
    /**
     * Soma total de despesas de uma categoria
     */
    @Query("SELECT SUM(e.value) FROM Expense e WHERE e.category.id = :categoryId")
    BigDecimal getTotalByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * Soma total de despesas em um período
     */
    @Query("SELECT SUM(e.value) FROM Expense e WHERE e.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // ==================== CONTADORES ====================
    
    /**
     * Conta despesas por categoria
     */
    long countByCategory(Category category);
    
    /**
     * Conta despesas por usuário
     */
    long countByUser(User user);
    
    /**
     * Conta despesas em um período
     */
    long countByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // ==================== QUERIES ESPECÍFICAS ====================
    
    /**
     * Busca as N despesas mais recentes
     */
    @Query("SELECT e FROM Expense e ORDER BY e.date DESC")
    List<Expense> findRecentExpenses();
    
    /**
     * Busca as N despesas mais recentes de um usuário
     */
    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId ORDER BY e.date DESC")
    List<Expense> findRecentExpensesByUser(@Param("userId") Long userId);
    
    /**
     * Busca as maiores despesas
     */
    @Query("SELECT e FROM Expense e ORDER BY e.value DESC")
    List<Expense> findHighestExpenses();
    
    /**
     * Busca despesas por usuário e categoria
     */
    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.category.id = :categoryId ORDER BY e.date DESC")
    List<Expense> findByUserIdAndCategoryId(
        @Param("userId") Long userId,
        @Param("categoryId") Long categoryId
    );
}