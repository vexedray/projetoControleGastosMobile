package com.expense.service;

import com.expense.model.Category;
import com.expense.model.Expense;
import com.expense.model.User;
import com.expense.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private Expense expense;
    private User user;
    private Category category;
    private List<Expense> expenseList;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("João Silva");
        user.setEmail("joao@email.com");

        category = new Category();
        category.setId(1L);
        category.setName("Alimentação");

        expense = new Expense();
        expense.setId(1L);
        expense.setValue(new BigDecimal("50.00"));
        expense.setDate(LocalDateTime.now());
        expense.setUser(user);
        expense.setCategory(category);

        Expense expense2 = new Expense();
        expense2.setId(2L);
        expense2.setValue(new BigDecimal("30.00"));
        expense2.setDate(LocalDateTime.now());
        expense2.setUser(user);
        expense2.setCategory(category);

        expenseList = Arrays.asList(expense, expense2);
    }

    @Test
    @DisplayName("Deve salvar nova despesa")
    void shouldSaveNewExpense() {
        // Given
        Expense newExpense = new Expense();
        newExpense.setValue(new BigDecimal("100.00"));
        newExpense.setUser(user);
        newExpense.setCategory(category);

        Expense savedExpense = new Expense();
        savedExpense.setId(3L);
        savedExpense.setValue(new BigDecimal("100.00"));
        savedExpense.setUser(user);
        savedExpense.setCategory(category);

        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        // When
        Expense result = expenseService.save(newExpense);

        // Then
        assertNotNull(result.getId());
        assertEquals(new BigDecimal("100.00"), result.getValue());
        verify(expenseRepository, times(1)).save(newExpense);
    }

    @Test
    @DisplayName("Deve atualizar despesa existente")
    void shouldUpdateExistingExpense() {
        // Given
        expense.setValue(new BigDecimal("75.00"));
        when(expenseRepository.save(expense)).thenReturn(expense);

        // When
        Expense result = expenseService.save(expense);

        // Then
        assertEquals(new BigDecimal("75.00"), result.getValue());
        verify(expenseRepository, times(1)).save(expense);
    }

    @Test
    @DisplayName("Deve retornar todas as despesas")
    void shouldReturnAllExpenses() {
        // Given
        when(expenseRepository.findAll()).thenReturn(expenseList);

        // When
        List<Expense> result = expenseService.findAll();

        // Then
        assertEquals(2, result.size());
        assertEquals(new BigDecimal("50.00"), result.get(0).getValue());
        verify(expenseRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve encontrar despesa por ID")
    void shouldFindExpenseById() {
        // Given
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        // When
        Optional<Expense> result = expenseService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("50.00"), result.get().getValue());
        verify(expenseRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando despesa não existe")
    void shouldReturnEmptyWhenExpenseNotFound() {
        // Given
        when(expenseRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Expense> result = expenseService.findById(99L);

        // Then
        assertFalse(result.isPresent());
        verify(expenseRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve encontrar despesas por usuário")
    void shouldFindExpensesByUser() {
        // Given
        when(expenseRepository.findByUser(user)).thenReturn(expenseList);

        // When
        List<Expense> result = expenseService.findByUser(user);

        // Then
        assertEquals(2, result.size());
        verify(expenseRepository, times(1)).findByUser(user);
    }

    @Test
    @DisplayName("Deve encontrar despesas por categoria")
    void shouldFindExpensesByCategory() {
        // Given
        when(expenseRepository.findByCategory(category)).thenReturn(expenseList);

        // When
        List<Expense> result = expenseService.findByCategory(category);

        // Then
        assertEquals(2, result.size());
        verify(expenseRepository, times(1)).findByCategory(category);
    }

    @Test
    @DisplayName("Deve encontrar despesas por ID do usuário")
    void shouldFindExpensesByUserId() {
        // Given
        when(expenseRepository.findByUser_Id(1L)).thenReturn(expenseList);

        // When
        List<Expense> result = expenseService.findByUserId(1L);

        // Then
        assertEquals(2, result.size());
        verify(expenseRepository, times(1)).findByUser_Id(1L);
    }

    @Test
    @DisplayName("Deve encontrar despesas por ID da categoria")
    void shouldFindExpensesByCategoryId() {
        // Given
        when(expenseRepository.findByCategory_Id(1L)).thenReturn(expenseList);

        // When
        List<Expense> result = expenseService.findByCategoryId(1L);

        // Then
        assertEquals(2, result.size());
        verify(expenseRepository, times(1)).findByCategory_Id(1L);
    }

    @Test
    @DisplayName("Deve encontrar despesas ordenadas por data")
    void shouldFindExpensesOrderedByDate() {
        // Given
        when(expenseRepository.findByUserIdOrderByDateDesc(1L)).thenReturn(expenseList);

        // When
        List<Expense> result = expenseService.findByUserIdOrderByDate(1L);

        // Then
        assertEquals(2, result.size());
        verify(expenseRepository, times(1)).findByUserIdOrderByDateDesc(1L);
    }

    @Test
    @DisplayName("Deve deletar despesa por ID")
    void shouldDeleteExpenseById() {
        // Given
        doNothing().when(expenseRepository).deleteById(1L);

        // When
        expenseService.deleteExpense(1L);

        // Then
        verify(expenseRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve verificar se despesa existe por ID")
    void shouldCheckIfExpenseExistsById() {
        // Given
        when(expenseRepository.existsById(1L)).thenReturn(true);

        // When
        boolean exists = expenseService.existsById(1L);

        // Then
        assertTrue(exists);
        verify(expenseRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("Deve calcular total de despesas por usuário")
    void shouldCalculateTotalByUser() {
        // Given
        when(expenseRepository.findByUser(user)).thenReturn(expenseList);

        // When
        BigDecimal total = expenseService.getTotalByUser(user);

        // Then
        assertEquals(new BigDecimal("80.00"), total);
        verify(expenseRepository, times(1)).findByUser(user);
    }

    @Test
    @DisplayName("Deve calcular total de despesas por ID do usuário")
    void shouldCalculateTotalByUserId() {
        // Given
        when(expenseRepository.findByUser_Id(1L)).thenReturn(expenseList);

        // When
        BigDecimal total = expenseService.getTotalByUserId(1L);

        // Then
        assertEquals(new BigDecimal("80.00"), total);
        verify(expenseRepository, times(1)).findByUser_Id(1L);
    }

    @Test
    @DisplayName("Deve calcular total de despesas por categoria")
    void shouldCalculateTotalByCategory() {
        // Given
        when(expenseRepository.findByCategory(category)).thenReturn(expenseList);

        // When
        BigDecimal total = expenseService.getTotalByCategory(category);

        // Then
        assertEquals(new BigDecimal("80.00"), total);
        verify(expenseRepository, times(1)).findByCategory(category);
    }

    @Test
    @DisplayName("Deve calcular total de despesas por ID da categoria")
    void shouldCalculateTotalByCategoryId() {
        // Given
        when(expenseRepository.findByCategory_Id(1L)).thenReturn(expenseList);

        // When
        BigDecimal total = expenseService.getTotalByCategoryId(1L);

        // Then
        assertEquals(new BigDecimal("80.00"), total);
        verify(expenseRepository, times(1)).findByCategory_Id(1L);
    }

    @Test
    @DisplayName("Deve retornar zero quando não há despesas para calcular total")
    void shouldReturnZeroWhenNoExpensesToCalculateTotal() {
        // Given
        when(expenseRepository.findByUser_Id(1L)).thenReturn(Arrays.asList());

        // When
        BigDecimal total = expenseService.getTotalByUserId(1L);

        // Then
        assertEquals(BigDecimal.ZERO, total);
        verify(expenseRepository, times(1)).findByUser_Id(1L);
    }

    @Test
    @DisplayName("Deve encontrar despesas por período")
    void shouldFindExpensesByDateRange() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        when(expenseRepository.findByDateBetween(startDate, endDate)).thenReturn(expenseList);

        // When
        List<Expense> result = expenseService.findByDateRange(startDate, endDate);

        // Then
        assertEquals(2, result.size());
        verify(expenseRepository, times(1)).findByDateBetween(startDate, endDate);
    }

    @Test
    @DisplayName("Deve encontrar despesas por usuário em um período")
    void shouldFindExpensesByUserAndDateRange() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        when(expenseRepository.findByUserAndDateBetween(user, startDate, endDate)).thenReturn(expenseList);

        // When
        List<Expense> result = expenseService.findByUserAndDateRange(user, startDate, endDate);

        // Then
        assertEquals(2, result.size());
        verify(expenseRepository, times(1)).findByUserAndDateBetween(user, startDate, endDate);
    }

    @Test
    @DisplayName("Deve calcular total de despesas por período")
    void shouldCalculateTotalByDateRange() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        when(expenseRepository.findByDateBetween(startDate, endDate)).thenReturn(expenseList);

        // When
        BigDecimal total = expenseService.getTotalByDateRange(startDate, endDate);

        // Then
        assertEquals(new BigDecimal("80.00"), total);
        verify(expenseRepository, times(1)).findByDateBetween(startDate, endDate);
    }

    @Test
    @DisplayName("Deve contar total de despesas")
    void shouldCountTotalExpenses() {
        // Given
        when(expenseRepository.count()).thenReturn(2L);

        // When
        long count = expenseService.count();

        // Then
        assertEquals(2L, count);
        verify(expenseRepository, times(1)).count();
    }

    @Test
    @DisplayName("Deve retornar totais por categoria")
    void shouldReturnTotalsByCategories() {
        // Given
        Object[] result = {"Alimentação", new BigDecimal("80.00")};
        List<Object[]> results = new ArrayList<>();
        results.add(result);
        when(expenseRepository.findExpensesByCategory()).thenReturn(results);

        // When
        Map<String, BigDecimal> totals = expenseService.getTotalByCategories();

        // Then
        assertNotNull(totals);
        assertEquals(new BigDecimal("80.00"), totals.get("Alimentação"));
        verify(expenseRepository, times(1)).findExpensesByCategory();
    }

    @Test
    @DisplayName("Deve encontrar despesas com valor maior que um limite")
    void shouldFindExpensesWithValueGreaterThan() {
        // Given
        BigDecimal limit = new BigDecimal("40.00");
        List<Expense> expensesAboveLimit = Arrays.asList(expense); // apenas a de 50.00
        when(expenseRepository.findByValueGreaterThan(limit)).thenReturn(expensesAboveLimit);

        // When
        List<Expense> result = expenseService.findByValueGreaterThan(limit);

        // Then
        assertEquals(1, result.size());
        assertEquals(new BigDecimal("50.00"), result.get(0).getValue());
        verify(expenseRepository, times(1)).findByValueGreaterThan(limit);
    }

    @Test
    @DisplayName("Deve encontrar despesas com valor entre limites")
    void shouldFindExpensesWithValueBetween() {
        // Given
        BigDecimal minValue = new BigDecimal("25.00");
        BigDecimal maxValue = new BigDecimal("75.00");
        when(expenseRepository.findByValueBetween(minValue, maxValue)).thenReturn(expenseList);

        // When
        List<Expense> result = expenseService.findByValueBetween(minValue, maxValue);

        // Then
        assertEquals(2, result.size());
        verify(expenseRepository, times(1)).findByValueBetween(minValue, maxValue);
    }

    @Test
    @DisplayName("Deve contar despesas por categoria")
    void shouldCountExpensesByCategory() {
        // Given
        when(expenseRepository.countByCategory(category)).thenReturn(2L);

        // When
        long count = expenseService.countByCategory(category);

        // Then
        assertEquals(2L, count);
        verify(expenseRepository, times(1)).countByCategory(category);
    }

    @Test
    @DisplayName("Deve contar despesas por usuário")
    void shouldCountExpensesByUser() {
        // Given
        when(expenseRepository.countByUser(user)).thenReturn(2L);

        // When
        long count = expenseService.countByUser(user);

        // Then
        assertEquals(2L, count);
        verify(expenseRepository, times(1)).countByUser(user);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há despesas")
    void shouldReturnEmptyListWhenNoExpenses() {
        // Given
        when(expenseRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Expense> result = expenseService.findAll();

        // Then
        assertTrue(result.isEmpty());
        verify(expenseRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve calcular total zero quando não há despesas em período")
    void shouldReturnZeroTotalWhenNoExpensesInDateRange() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);
        when(expenseRepository.findByDateBetween(startDate, endDate)).thenReturn(Arrays.asList());

        // When
        BigDecimal total = expenseService.getTotalByDateRange(startDate, endDate);

        // Then
        assertEquals(BigDecimal.ZERO, total);
        verify(expenseRepository, times(1)).findByDateBetween(startDate, endDate);
    }
}