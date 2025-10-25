package com.expense.service;

import com.expense.model.Expense;
import com.expense.model.Category;
import com.expense.model.User;
import com.expense.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpenseService.class);
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    /**
     * Salva ou atualiza uma despesa
     */
    public Expense save(Expense expense) {
        if (expense.getId() == null) {
            logger.debug("Criando nova despesa: valor={}", expense.getValue());
        } else {
            logger.debug("Atualizando despesa ID {}: valor={}", expense.getId(), expense.getValue());
        }
        Expense saved = expenseRepository.save(expense);
        logger.debug("Despesa salva com ID: {}", saved.getId());
        return saved;
    }
    
    /**
     * Busca todas as despesas
     */
    public List<Expense> findAll() {
        logger.debug("Buscando todas as despesas");
        List<Expense> expenses = expenseRepository.findAll();
        logger.debug("Encontradas {} despesas", expenses.size());
        return expenses;
    }
    
    /**
     * Busca despesa por ID
     */
    public Optional<Expense> findById(Long id) {
        logger.debug("Buscando despesa com ID: {}", id);
        Optional<Expense> expense = expenseRepository.findById(id);
        if (expense.isPresent()) {
            logger.debug("Despesa encontrada: valor={}", expense.get().getValue());
        } else {
            logger.debug("Despesa com ID {} não encontrada", id);
        }
        return expense;
    }
    
    /**
     * Busca despesas por usuário (objeto)
     */
    public List<Expense> findByUser(User user) {
        logger.debug("Buscando despesas do usuário: {}", user.getId());
        List<Expense> expenses = expenseRepository.findByUser(user);
        logger.debug("Encontradas {} despesas para o usuário {}", expenses.size(), user.getId());
        return expenses;
    }
    
    /**
     * Busca despesas por categoria (objeto)
     */
    public List<Expense> findByCategory(Category category) {
        logger.debug("Buscando despesas da categoria: {}", category.getId());
        List<Expense> expenses = expenseRepository.findByCategory(category);
        logger.debug("Encontradas {} despesas para a categoria {}", expenses.size(), category.getId());
        return expenses;
    }
    
    /**
     * Busca despesas por ID do usuário
     */
    public List<Expense> findByUserId(Long userId) {
        logger.debug("Buscando despesas do usuário ID: {}", userId);
        return expenseRepository.findByUser_Id(userId);
    }
    
    /**
     * Busca despesas por ID da categoria
     */
    public List<Expense> findByCategoryId(Long categoryId) {
        logger.debug("Buscando despesas da categoria ID: {}", categoryId);
        return expenseRepository.findByCategory_Id(categoryId);
    }
    
    /**
     * Busca despesas por usuário ordenadas por data (mais recente primeiro)
     */
    public List<Expense> findByUserIdOrderByDate(Long userId) {
        logger.debug("Buscando despesas do usuário {} ordenadas por data", userId);
        return expenseRepository.findByUserIdOrderByDateDesc(userId);
    }
    
    /**
     * Busca despesas por categoria ordenadas por data
     */
    public List<Expense> findByCategoryIdOrderByDate(Long categoryId) {
        logger.debug("Buscando despesas da categoria {} ordenadas por data", categoryId);
        return expenseRepository.findByCategoryIdOrderByDateDesc(categoryId);
    }
    
    /**
     * Deleta despesa por ID
     */
    public void deleteExpense(Long id) {
        logger.debug("Deletando despesa com ID: {}", id);
        expenseRepository.deleteById(id);
        logger.debug("Despesa {} deletada com sucesso", id);
    }
    
    /**
     * Verifica se despesa existe por ID
     */
    public boolean existsById(Long id) {
        logger.debug("Verificando existência da despesa com ID: {}", id);
        boolean exists = expenseRepository.existsById(id);
        logger.debug("Despesa {} existe: {}", id, exists);
        return exists;
    }
    
    /**
     * Conta total de despesas
     */
    public long count() {
        logger.debug("Contando total de despesas");
        long total = expenseRepository.count();
        logger.debug("Total de despesas: {}", total);
        return total;
    }
    
    // ==================== MÉTODOS DE AGREGAÇÃO E ESTATÍSTICAS ====================
    
    /**
     * Calcula o total de despesas de um usuário
     */
    public BigDecimal getTotalByUser(User user) {
        logger.debug("Calculando total de despesas do usuário: {}", user.getId());
        List<Expense> expenses = expenseRepository.findByUser(user);
        BigDecimal total = expenses.stream()
                .map(Expense::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.debug("Total de despesas do usuário {}: {}", user.getId(), total);
        return total;
    }
    
    /**
     * Calcula o total de despesas de um usuário por ID
     */
    public BigDecimal getTotalByUserId(Long userId) {
        logger.debug("Calculando total de despesas do usuário ID: {}", userId);
        List<Expense> expenses = expenseRepository.findByUser_Id(userId);
        BigDecimal total = expenses.stream()
                .map(Expense::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.debug("Total de despesas do usuário {}: {}", userId, total);
        return total;
    }
    
    /**
     * Calcula o total de despesas de uma categoria
     */
    public BigDecimal getTotalByCategory(Category category) {
        logger.debug("Calculando total de despesas da categoria: {}", category.getId());
        List<Expense> expenses = expenseRepository.findByCategory(category);
        BigDecimal total = expenses.stream()
                .map(Expense::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.debug("Total de despesas da categoria {}: {}", category.getId(), total);
        return total;
    }
    
    /**
     * Calcula o total de despesas de uma categoria por ID
     */
    public BigDecimal getTotalByCategoryId(Long categoryId) {
        logger.debug("Calculando total de despesas da categoria ID: {}", categoryId);
        List<Expense> expenses = expenseRepository.findByCategory_Id(categoryId);
        BigDecimal total = expenses.stream()
                .map(Expense::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.debug("Total de despesas da categoria {}: {}", categoryId, total);
        return total;
    }
    
    /**
     * Retorna despesas agrupadas por categoria (formato antigo para compatibilidade)
     */
    public Map<String, Object> getExpensesByTypeGrouped() {
        logger.debug("Agrupando despesas por categoria");
        List<Object[]> results = expenseRepository.findExpensesByCategory();
        Map<String, Object> groupedExpenses = new HashMap<>();
        
        for (Object[] result : results) {
            groupedExpenses.put((String) result[0], result[1]);
        }
        
        logger.debug("Despesas agrupadas em {} categorias", groupedExpenses.size());
        return groupedExpenses;
    }
    
    /**
     * Retorna totais por categoria
     */
    public Map<String, BigDecimal> getTotalByCategories() {
        logger.debug("Calculando total por categorias");
        List<Object[]> results = expenseRepository.findExpensesByCategory();
        Map<String, BigDecimal> totals = new HashMap<>();
        
        for (Object[] result : results) {
            String categoryName = (String) result[0];
            BigDecimal total = (BigDecimal) result[1];
            totals.put(categoryName, total);
        }
        
        logger.debug("Totais calculados para {} categorias", totals.size());
        return totals;
    }
    
    /**
     * Busca despesas por período
     */
    public List<Expense> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        logger.debug("Buscando despesas entre {} e {}", startDate, endDate);
        List<Expense> expenses = expenseRepository.findByDateBetween(startDate, endDate);
        logger.debug("Encontradas {} despesas no período", expenses.size());
        return expenses;
    }
    
    /**
     * Busca despesas de um usuário em um período
     */
    public List<Expense> findByUserAndDateRange(User user, LocalDateTime startDate, LocalDateTime endDate) {
        logger.debug("Buscando despesas do usuário {} entre {} e {}", user.getId(), startDate, endDate);
        List<Expense> expenses = expenseRepository.findByUserAndDateBetween(user, startDate, endDate);
        logger.debug("Encontradas {} despesas do usuário {} no período", expenses.size(), user.getId());
        return expenses;
    }
    
    /**
     * Busca despesas de um usuário em um período (por ID)
     */
    public List<Expense> findByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.debug("Buscando despesas do usuário ID {} entre {} e {}", userId, startDate, endDate);
        return expenseRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    }
    
    /**
     * Calcula total de despesas em um período
     */
    public BigDecimal getTotalByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        logger.debug("Calculando total de despesas entre {} e {}", startDate, endDate);
        List<Expense> expenses = expenseRepository.findByDateBetween(startDate, endDate);
        BigDecimal total = expenses.stream()
                .map(Expense::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.debug("Total no período: {}", total);
        return total;
    }
    
    /**
     * Calcula total de despesas de um usuário em um período
     */
    public BigDecimal getTotalByUserAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.debug("Calculando total do usuário {} entre {} e {}", userId, startDate, endDate);
        List<Expense> expenses = expenseRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        BigDecimal total = expenses.stream()
                .map(Expense::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.debug("Total do usuário {} no período: {}", userId, total);
        return total;
    }
    
    /**
     * Busca despesas com valor acima de um limite
     */
    public List<Expense> findByValueGreaterThan(BigDecimal value) {
        logger.debug("Buscando despesas com valor maior que {}", value);
        return expenseRepository.findByValueGreaterThan(value);
    }
    
    /**
     * Busca despesas com valor entre dois limites
     */
    public List<Expense> findByValueBetween(BigDecimal minValue, BigDecimal maxValue) {
        logger.debug("Buscando despesas com valor entre {} e {}", minValue, maxValue);
        return expenseRepository.findByValueBetween(minValue, maxValue);
    }
    
    /**
     * Conta despesas por categoria
     */
    public long countByCategory(Category category) {
        logger.debug("Contando despesas da categoria: {}", category.getId());
        return expenseRepository.countByCategory(category);
    }
    
    /**
     * Conta despesas por usuário
     */
    public long countByUser(User user) {
        logger.debug("Contando despesas do usuário: {}", user.getId());
        return expenseRepository.countByUser(user);
    }
}